package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.EventTracker;

public class BlockBreakEventListener extends EventListener {

    public BlockBreakEventListener(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final BlockBreakEvent event) {
        if (event.isCancelled()) return;

        this.tracker.record(event.getPlayer(), event);
    }

}
