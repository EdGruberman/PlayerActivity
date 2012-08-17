package edgruberman.bukkit.playeractivity.messaging;

import java.util.logging.Level;

import org.bukkit.Server;

/**
 * all players in server at message delivery time
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 2.0.0
 */
public class ServerPlayers extends PermissionSubscribers {

    public ServerPlayers() {
        super(Server.BROADCAST_CHANNEL_USERS);
    }

    @Override
    public Confirmation deliver(final Message message) {
        final Confirmation confirmation = super.deliver(message);
        return new Confirmation(Level.FINEST, confirmation.getReceived(), "[BROADCAST({1})] {0}", message, confirmation.getReceived());
    }

}
