package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PaintingBreakByEntityEvent extends Interpreter {

    public PaintingBreakByEntityEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.painting.PaintingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) return;

        this.record((Player) event.getRemover(), event);
    }

}
