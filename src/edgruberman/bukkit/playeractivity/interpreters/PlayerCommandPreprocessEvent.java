package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerCommandPreprocessEvent extends PlayerEvent {

    public PlayerCommandPreprocessEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerCommandPreprocessEvent.class);
    }

}
