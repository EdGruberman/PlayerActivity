package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerChatTabCompleteEvent extends PlayerEvent {

    public PlayerChatTabCompleteEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerChatTabCompleteEvent.class);
    }

}
