package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerPickupItemEvent extends PlayerEvent {

    public PlayerPickupItemEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerPickupItemEvent.class);
    }

}
