package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerDropItemEvent extends PlayerEvent {

    public PlayerDropItemEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerDropItemEvent.class);
    }

}
