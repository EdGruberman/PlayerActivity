package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class AsyncPlayerChatEvent extends Interpreter {

    public AsyncPlayerChatEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.player.AsyncPlayerChatEvent event) {
        this.record(event.getPlayer(), event);
    }

}
