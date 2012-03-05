package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.EventFilter;
import edgruberman.bukkit.playeractivity.EventTracker;

public class PlayerLoginEvent extends EventFilter {

    public PlayerLoginEvent(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final org.bukkit.event.player.PlayerLoginEvent event) {
        this.tracker.record(event.getPlayer(), event);
    }

}
