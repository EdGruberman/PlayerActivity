package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerChangedWorldEvent extends PlayerEvent {

    public PlayerChangedWorldEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerChangedWorldEvent.class);
    }

}
