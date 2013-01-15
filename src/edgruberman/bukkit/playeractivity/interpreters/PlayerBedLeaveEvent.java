package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerBedLeaveEvent extends PlayerEvent {

    public PlayerBedLeaveEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerBedLeaveEvent.class);
    }

}
