package edgruberman.bukkit.playeractivity.messaging;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * handles message delivery and logging
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 2.0.0
 */
public class Courier {

    protected final Plugin plugin;
    protected final boolean timestamp;

    /** prepends a timestamp parameter for all messages */
    public Courier(final Plugin plugin) {
        this.plugin = plugin;
        this.timestamp = true;
    }

    protected Courier(final Courier.Factory parameters) {
        this.plugin = parameters.plugin;
        this.timestamp = parameters.timestamp;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    /** true if all messages will have their arguments automatically prepended with the current date/time */
    public boolean getTimestamp() {
        return this.timestamp;
    }

    /** Message construction (timestamps prepended if configured) */
    public List<Message> draft(final String key, final Object... arguments) {
        if (key == null) return Collections.emptyList();

        final Message.Factory factory = Message.Factory.create(key, arguments);
        if (this.timestamp) factory.timestamp();
        final List<Message> messages = new ArrayList<Message>();
        messages.add(factory.build());
        return messages;
    }

    /** deliver messages to recipients and record log entry for each message (this will not timestamp the message) */
    public void submit(final Recipients recipients, final List<Message> messages) {
        for (final Message message : messages) {
            final Confirmation confirmation;
            try {
                confirmation = recipients.deliver(message);

            } catch (final RuntimeException e) {
                this.plugin.getLogger().warning("Error submitting message for delivery; pattern: \"" + message.original + "\"; " + e);
                continue;
            }

            this.plugin.getLogger().log(confirmation.toLogRecord());
        }
    }

    /** deliver single message to recipients and record log entry (this will not timestamp the message) */
    public void submit(final Recipients recipients, final Message message) {
        this.submit(recipients, Arrays.asList(message));
    }

    /** deliver message to individual player */
    public void send(final CommandSender sender, final String key, final Object... arguments) {
        final Recipients recipients = new Sender(sender);
        final List<Message> messages = this.draft(key, arguments);
        this.submit(recipients, messages);
    }

    /** deliver message to all players on server */
    public void broadcast(final String key, final Object... arguments) {
        final Recipients recipients = new ServerPlayers();
        final List<Message> messages = this.draft(key, arguments);
        this.submit(recipients, messages);
    }

    /** deliver message to players in a world */
    public void world(final World world, final String key, final Object... arguments) {
        final Recipients recipients = new WorldPlayers(world);
        final List<Message> messages = this.draft(key, arguments);
        this.submit(recipients, messages);
    }

    /** deliver message to players with a permission */
    public void publish(final String permission, final String key, final Object... arguments) {
        final Recipients recipients = new PermissionSubscribers(permission);
        final List<Message> messages = this.draft(key, arguments);
        this.submit(recipients, messages);
    }

    /** format a pattern with supplied arguments */
    public String format(final String key, final Object... arguments) {
        return MessageFormat.format(key, arguments);
    }



    public static class Factory {

        /** prepends a timestamp to all messages */
        public static Factory create(final Plugin plugin) {
            return new Factory(plugin);
        }

        public Plugin plugin;
        public boolean timestamp;

        protected Factory(final Plugin plugin) {
            this.plugin = plugin;
            this.setTimestamp(true);
        }

        /** @param timestamp true to prepend timestamp to arguments of all messages */
        public Factory setTimestamp(final boolean timestamp) {
            this.timestamp = true;
            return this;
        }

        public Courier build() {
            return new Courier(this);
        }

    }

}
