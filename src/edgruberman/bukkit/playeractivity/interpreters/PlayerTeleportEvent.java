package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerTeleportEvent extends PlayerEvent {

    public PlayerTeleportEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerTeleportEvent.class);
    }

}
