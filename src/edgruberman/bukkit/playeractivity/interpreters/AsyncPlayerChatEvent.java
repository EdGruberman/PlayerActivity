package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class AsyncPlayerChatEvent extends PlayerEvent {

    public AsyncPlayerChatEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.AsyncPlayerChatEvent.class);
    }

}
