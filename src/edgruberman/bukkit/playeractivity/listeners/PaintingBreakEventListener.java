package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.EventTracker;

public class PaintingBreakEventListener extends EventListener {

    public PaintingBreakEventListener(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final PaintingBreakEvent event) {
        if (event.isCancelled()) return;

        if (!(event instanceof PaintingBreakByEntityEvent)) return;

        final PaintingBreakByEntityEvent pbbee = (PaintingBreakByEntityEvent) event;
        if (!(pbbee.getRemover() instanceof Player)) return;

        final Player player = (Player) pbbee.getRemover();
        this.tracker.record(player, event);
    }

}
