package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerBedEnterEvent extends PlayerEvent {

    public PlayerBedEnterEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerBedEnterEvent.class);
    }

}
