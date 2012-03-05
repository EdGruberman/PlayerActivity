package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PaintingBreakByEntityEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final org.bukkit.event.painting.PaintingBreakByEntityEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getRemover() instanceof Player)) return;

        final Player player = (Player) event.getRemover();
        this.tracker.record(player, event);
    }

}
