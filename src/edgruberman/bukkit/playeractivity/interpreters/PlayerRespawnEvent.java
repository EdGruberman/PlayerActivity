package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerRespawnEvent extends PlayerEvent {

    public PlayerRespawnEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerRespawnEvent.class);
    }

}
