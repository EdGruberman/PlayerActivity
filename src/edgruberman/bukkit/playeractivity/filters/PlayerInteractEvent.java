package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.EventFilter;
import edgruberman.bukkit.playeractivity.EventTracker;

public class PlayerInteractEvent extends EventFilter {

    public PlayerInteractEvent(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final org.bukkit.event.player.PlayerInteractEvent event) {
        // TODO - use event.isCancelled() when bug is fixed that doesn't check right clicking on air with item returning true
        if (event.useInteractedBlock() == Result.DENY && event.useItemInHand() == Result.DENY) return;

        this.tracker.record(event.getPlayer(), event);
    }

}
