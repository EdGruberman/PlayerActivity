package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerLoginEvent extends PlayerEvent {

    public PlayerLoginEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerLoginEvent.class);
    }

}
