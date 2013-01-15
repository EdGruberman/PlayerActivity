package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerShearEntityEvent extends PlayerEvent {

    public PlayerShearEntityEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerShearEntityEvent.class);
    }

}
