package edgruberman.bukkit.messaging;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messaging.messages.Confirmation;
import edgruberman.bukkit.messaging.recipients.PermissionSubscribers;
import edgruberman.bukkit.messaging.recipients.Sender;
import edgruberman.bukkit.messaging.recipients.ServerPlayers;
import edgruberman.bukkit.messaging.recipients.WorldPlayers;

/** handles plugin registration to log message delivery */
public class Courier {

    protected final Plugin plugin;

    public Courier(final Plugin plugin) {
        this.plugin = plugin;
    }

    public void deliver(final Recipients recipients, final Message message) {
        if (message.format == null) return;

        final Confirmation confirmation = recipients.send(message);
        if (this.plugin.getLogger().isLoggable(confirmation.getLevel()))
            this.plugin.getLogger().log(confirmation.toLogRecord());
    }

    public void send(final CommandSender sender, final String format, final Object... args) {
        final Recipients recipients = new Sender(sender);
        final Message message = new Message(format, args);
        this.deliver(recipients, message);
    }

    public void broadcast(final String format, final Object... args) {
        final Recipients recipients = new ServerPlayers();
        final Message message = new Message(format, args);
        this.deliver(recipients, message);
    }

    public void world(final World world, final String format, final Object... args) {
        final Recipients recipients = new WorldPlayers(world);
        final Message message = new Message(format, args);
        this.deliver(recipients, message);
    }

    public void publish(final String permission, final String format, final Object... args) {
        final Recipients recipients = new PermissionSubscribers(permission);
        final Message message = new Message(format, args);
        this.deliver(recipients, message);
    }

}
