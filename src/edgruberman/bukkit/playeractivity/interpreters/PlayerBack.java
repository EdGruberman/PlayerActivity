package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerBack extends PlayerEvent {

    public PlayerBack(final StatusTracker tracker) {
        super(tracker, edgruberman.bukkit.playeractivity.consumers.away.PlayerBack.class);
    }

}
