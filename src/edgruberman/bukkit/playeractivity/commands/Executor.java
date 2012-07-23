package edgruberman.bukkit.playeractivity.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

abstract class Executor implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return this.execute(sender, command, label, Executor.transform(args));
    }

    protected abstract boolean execute(final CommandSender sender, final Command command, final String label, final List<String> args);

    /**
     * Concatenate arguments to compensate for double quotes indicating single
     * argument, removing any delimiting double quotes.
     *
     * TODO use \ for escaping double quote characters
     * TODO make this less messy
     */
    private static List<String> transform(final String[] args) {
        final List<String> arguments = new ArrayList<String>();

        String previous = null;
        for (final String arg : args) {
            if (previous != null) {
                if (arg.endsWith("\"")) {
                    arguments.add(Executor.stripDoubleQuotes(previous + " " + arg));
                    previous = null;
                } else {
                    previous += " " + arg;
                }
                continue;
            }

            if (arg.startsWith("\"") && !arg.endsWith("\"")) {
                previous = arg;
            } else {
                arguments.add(Executor.stripDoubleQuotes(arg));
            }
        }
        if (previous != null) arguments.add(Executor.stripDoubleQuotes(previous));

        return arguments;
    }

    private static String stripDoubleQuotes(final String s) {
        return Executor.stripDelimiters(s, "\"");
    }

    private static String stripDelimiters(final String s, final String delim) {
        if (!s.startsWith(delim) || !s.endsWith(delim)) return s;

        return s.substring(1, s.length() - 1);
    }

}
