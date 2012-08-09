package edgruberman.bukkit.messaging;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/** message format string and arguments that are able to be customized for each target */
public class Message {

    protected final String format;
    protected final Object[] args;

    public Message(final String format, final Object... args) {
        this.format = format;
        this.args = args;
    }

    public String formatFor(final CommandSender target) {
        return String.format(this.format, this.args);
    }

    @Override
    public String toString() {
        return this.formatFor(Bukkit.getConsoleSender());
    }

}
