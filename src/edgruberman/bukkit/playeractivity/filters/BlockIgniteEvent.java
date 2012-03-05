package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.Interpreter;

public class BlockIgniteEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final org.bukkit.event.block.BlockIgniteEvent event) {
        if (event.isCancelled()) return;

        this.tracker.record(event.getPlayer(), event);
    }

}
