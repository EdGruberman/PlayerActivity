package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PaintingBreakByEntityEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.painting.PaintingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) return;

        this.record((Player) event.getRemover(), event);
    }

}
