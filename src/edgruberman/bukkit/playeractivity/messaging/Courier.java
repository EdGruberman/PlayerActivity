package edgruberman.bukkit.playeractivity.messaging;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.messaging.messages.Confirmation;
import edgruberman.bukkit.playeractivity.messaging.recipients.PermissionSubscribers;
import edgruberman.bukkit.playeractivity.messaging.recipients.Sender;
import edgruberman.bukkit.playeractivity.messaging.recipients.ServerPlayers;
import edgruberman.bukkit.playeractivity.messaging.recipients.WorldPlayers;

/**
 * handles message delivery and logging
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 1.0.0
 */
public class Courier {

    protected final Plugin plugin;

    public Courier(final Plugin plugin) {
        this.plugin = plugin;
    }

    /** deliver a message to recipients and record log entry */
    public void submit(final Recipients recipients, final Message message) {
        if (message.original == null) return;

        final Confirmation confirmation = recipients.deliver(message);
        this.plugin.getLogger().log(confirmation.toLogRecord());
    }



    // ---- convenience methods ----

    /** deliver message to individual player */
    public void send(final CommandSender sender, final String format, final Object... arguments) {
        final Recipients recipients = new Sender(sender);
        final Message message = new Message(format, arguments);
        this.submit(recipients, message);
    }

    /** deliver message to all players on server */
    public void broadcast(final String format, final Object... arguments) {
        final Recipients recipients = new ServerPlayers();
        final Message message = new Message(format, arguments);
        this.submit(recipients, message);
    }

    /** deliver message to players in a world */
    public void world(final World world, final String format, final Object... arguments) {
        final Recipients recipients = new WorldPlayers(world);
        final Message message = new Message(format, arguments);
        this.submit(recipients, message);
    }

    /** deliver message to players with a permission */
    public void publish(final String permission, final String format, final Object... arguments) {
        final Recipients recipients = new PermissionSubscribers(permission);
        final Message message = new Message(format, arguments);
        this.submit(recipients, message);
    }

}
