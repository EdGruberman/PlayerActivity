package edgruberman.bukkit.playeractivity.messaging;

import java.text.MessageFormat;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * handles message delivery and logging
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 5.0.1
 */
public class Courier {

    protected final Plugin plugin;
    protected final boolean timestamp;
    protected final char colorCode;

    protected Courier(final Courier.Factory parameters) {
        this.plugin = parameters.plugin;
        this.timestamp = parameters.timestamp;
        this.colorCode = parameters.colorCode;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    /** @return true if all messages will have their arguments automatically prepended with the current date/time */
    public boolean getTimestamp() {
        return this.timestamp;
    }

    /** @return translated pattern if alternate color code configured, otherwise pattern */
    public String colorize(final String pattern) {
        return ( this.colorCode == ChatColor.COLOR_CHAR ? pattern : ChatColor.translateAlternateColorCodes(this.colorCode, pattern) );
    }

    /** format a pattern with supplied arguments */
    public String format(final String pattern, final Object... arguments) {
        return MessageFormat.format(this.colorize(pattern), arguments);
    }

    /**
     * preliminary Message construction before formatting for target recipient (timestamp argument prepended if configured)
     * @param pattern message text that contains format elements
     */
    public Message draft(final String pattern, final Object... arguments) {
        final Message.Factory factory = Message.create(this.colorize(pattern), arguments);
        if (this.timestamp) factory.timestamp();
        return factory.build();
    }

    /** deliver message to recipients and record log entry (this will not timestamp the message) */
    public void submit(final Recipients recipients, final Message message) {
        try {
            final Confirmation confirmation = recipients.deliver(message);
            this.plugin.getLogger().log(confirmation.toLogRecord());

        } catch (final RuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "Error submitting message for delivery; pattern: \"{0}\"; {1}", new Object[] { message.original, e });
        }
    }

    /** deliver message to individual player */
    public void sendMessage(final CommandSender sender, final String pattern, final Object... arguments) {
        final Recipients recipients = new Individual(sender);
        final Message message = this.draft(pattern, arguments);
        this.submit(recipients, message);
    }

    /** deliver message to all players on server */
    public void broadcastMessage(final String pattern, final Object... arguments) {
        final Recipients recipients = new ServerPlayers();
        final Message message = this.draft(pattern, arguments);
        this.submit(recipients, message);
    }

    /** deliver message to players in a world */
    public void worldMessage(final World world, final String pattern, final Object... arguments) {
        final Recipients recipients = new WorldPlayers(world);
        final Message message = this.draft(pattern, arguments);
        this.submit(recipients, message);
    }

    /** deliver message to players with a permission */
    public void publishMessage(final String permission, final String pattern, final Object... arguments) {
        final Recipients recipients = new PermissionSubscribers(permission);
        final Message message = this.draft(pattern, arguments);
        this.submit(recipients, message);
    }



    public static Factory create(final Plugin plugin) {
        return Factory.create(plugin);
    }

    public static class Factory {

        /** prepends a timestamp to all messages */
        public static Factory create(final Plugin plugin) {
            return new Factory(plugin);
        }

        public Plugin plugin;
        public boolean timestamp;
        public char colorCode;

        protected Factory(final Plugin plugin) {
            this.plugin = plugin;
            this.setTimestamp(true);
            this.colorCode = ChatColor.COLOR_CHAR;
        }

        /** @param timestamp true to prepend timestamp to arguments of all messages */
        public Factory setTimestamp(final boolean timestamp) {
            this.timestamp = true;
            return this;
        }

        /** @param colorCode prefix that designates a color code in message patterns (default is {@value org.bukkit.ChatColor#COLOR_CHAR}, common alternate is &) */
        public Factory setColorCode(final char colorCode) {
            this.colorCode = colorCode;
            return this;
        }

        public Courier build() {
            return new Courier(this);
        }

    }

}
