package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerItemHeldEvent extends PlayerEvent {

    public PlayerItemHeldEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerItemHeldEvent.class);
    }

}
