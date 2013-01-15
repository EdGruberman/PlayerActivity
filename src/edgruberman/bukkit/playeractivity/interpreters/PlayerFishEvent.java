package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerFishEvent extends PlayerEvent {

    public PlayerFishEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerFishEvent.class);
    }

}
