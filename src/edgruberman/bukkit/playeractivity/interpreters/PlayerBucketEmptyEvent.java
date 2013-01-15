package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerBucketEmptyEvent extends PlayerEvent {

    public PlayerBucketEmptyEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerBucketEmptyEvent.class);
    }

}
