package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerPortalEvent extends PlayerEvent {

    public PlayerPortalEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerPortalEvent.class);
    }

}
