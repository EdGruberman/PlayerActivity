package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerMoveEvent extends PlayerEvent {

    public PlayerMoveEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerMoveEvent.class);
    }

}
