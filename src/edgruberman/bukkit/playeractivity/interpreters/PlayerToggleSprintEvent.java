package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerToggleSprintEvent extends PlayerEvent {

    public PlayerToggleSprintEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerToggleSprintEvent.class);
    }

}
