package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.Interpreter;

public class VehicleExitEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final org.bukkit.event.vehicle.VehicleExitEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getExited() instanceof Player)) return;

        final Player player = (Player) event.getExited();
        this.tracker.record(player, event);
    }

}
