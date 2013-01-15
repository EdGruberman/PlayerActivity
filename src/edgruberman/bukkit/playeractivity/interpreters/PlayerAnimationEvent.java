package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerAnimationEvent extends PlayerEvent {

    public PlayerAnimationEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerAnimationEvent.class);
    }

}
