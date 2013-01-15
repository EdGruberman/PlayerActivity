package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerInteractEntityEvent extends PlayerEvent {

    public PlayerInteractEntityEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerInteractEntityEvent.class);
    }

}
