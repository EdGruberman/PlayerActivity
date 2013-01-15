package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerToggleFlightEvent extends PlayerEvent {

    public PlayerToggleFlightEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerToggleFlightEvent.class);
    }

}
