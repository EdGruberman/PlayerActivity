package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerJoinEvent extends PlayerEvent {

    public PlayerJoinEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerJoinEvent.class);
    }

}
