package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerMoveBlockEvent extends PlayerEvent {

    public PlayerMoveBlockEvent(final StatusTracker tracker) {
        super(tracker, edgruberman.bukkit.playeractivity.PlayerMoveBlockEvent.class);
    }

}
