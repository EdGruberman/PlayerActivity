package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerEggThrowEvent extends PlayerEvent {

    public PlayerEggThrowEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerEggThrowEvent.class);
    }

}
