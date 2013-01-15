package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerBucketFillEvent extends PlayerEvent {

    public PlayerBucketFillEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerBucketFillEvent.class);
    }

}
