package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class InventoryOpenEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.inventory.InventoryOpenEvent event) {
        if (!(event instanceof Player)) return;

        this.record((Player) event.getPlayer(), event);
    }

}
