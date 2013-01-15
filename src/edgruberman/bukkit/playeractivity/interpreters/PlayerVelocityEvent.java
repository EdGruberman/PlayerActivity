package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerVelocityEvent extends PlayerEvent {

    public PlayerVelocityEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerVelocityEvent.class);
    }

}
