package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerGameModeChangeEvent extends PlayerEvent {

    public PlayerGameModeChangeEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerGameModeChangeEvent.class);
    }

}
