package edgruberman.bukkit.messaging;

import edgruberman.bukkit.messaging.messages.Confirmation;

public interface Recipients {

    public abstract Confirmation send(Message message);

}
