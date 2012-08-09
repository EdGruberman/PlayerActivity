package edgruberman.bukkit.messaging.recipients;

import java.util.logging.Level;

import org.bukkit.Server;

import edgruberman.bukkit.messaging.Message;
import edgruberman.bukkit.messaging.messages.Confirmation;

public class ServerPlayers extends PermissionSubscribers {

    public ServerPlayers() {
        super(Server.BROADCAST_CHANNEL_USERS);
    }

    @Override
    public Confirmation send(final Message message) {
        final Confirmation confirmation = super.send(message);
        return new ServerConfirmation(message.toString(), confirmation.getReceived());
    }



    public class ServerConfirmation extends Confirmation {

        public ServerConfirmation(final String message, final int count) {
            super(Level.FINEST, count, "[BROADCAST(%2$d)] %1$s", message, count);

        }

    }

}
