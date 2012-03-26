package edgruberman.bukkit.playeractivity.commands.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

/**
 * Individual command execution request
 */
public class Context {

    public Handler handler;
    public CommandSender sender;
    public String label;
    public List<String> arguments;
    public Action action;

    /**
     * Parse a command execution request.
     *
     * @param handler command handler
     * @param sender command sender
     * @param args passed command arguments
     */
    Context(final Handler handler, final CommandSender sender, final String label, final String[] args) {
        this.handler = handler;
        this.sender = sender;
        this.label = label;
        this.arguments = Context.parseArguments(args);
        this.action = this.parseAction();
        this.handler.command.getPlugin().getLogger().log(Level.FINEST, "Command issued: " + this.toString());
    }

    /**
     * Identify requested action.
     *
     * @return the most specific matching action or the default action if none applies
     */
    private Action parseAction() {
        final Action action = this.parseAction(this.handler.actions);
        if (action != null) return action;

        return this.handler.getDefaultAction();
    }

    /**
     * Iterate any sub-actions to find most specific match.
     *
     * @param actions actions to check if they match
     * @return action that matches this context; null if no actions match
     */
    private Action parseAction(final List<Action> actions) {
        for (final Action parent : actions)
            if (parent.matches(this)) {
                final Action child = this.parseAction(parent.children);
                if (child != null) return child;

                return parent;
            }

        return null;
    }

    @Override
    public String toString() {
        return "Context [handler=" + this.handler + ", sender=" + this.sender.getName() + ", label=" + this.label + ", arguments=" + this.arguments + ", action=" + this.action + "]";
    }

    /**
     * Concatenate arguments to compensate for double quotes indicating single
     * argument, removing any delimiting double quotes.
     *
     * @return arguments
     *
     * TODO use \ for escaping double quote characters
     * TODO make this less messy
     */
    private static List<String> parseArguments(final String[] args) {
        final List<String> arguments = new ArrayList<String>();

        String previous = null;
        for (final String arg : args) {
            if (previous != null) {
                if (arg.endsWith("\"")) {
                    arguments.add(Context.stripDoubleQuotes(previous + " " + arg));
                    previous = null;
                } else {
                    previous += " " + arg;
                }
                continue;
            }

            if (arg.startsWith("\"") && !arg.endsWith("\"")) {
                previous = arg;
            } else {
                arguments.add(Context.stripDoubleQuotes(arg));
            }
        }
        if (previous != null) arguments.add(Context.stripDoubleQuotes(previous));

        return arguments;
    }

    private static String stripDoubleQuotes(final String s) {
        return Context.stripDelimiters(s, "\"");
    }

    private static String stripDelimiters(final String s, final String delim) {
        if (!s.startsWith(delim) || !s.endsWith(delim)) return s;

        return s.substring(1, s.length() - 1);
    }

}
