package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerToggleSneakEvent extends PlayerEvent {

    public PlayerToggleSneakEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerToggleSneakEvent.class);
    }

}
