package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.Interpreter;

public class VehicleDestroyEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final org.bukkit.event.vehicle.VehicleDestroyEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getAttacker() instanceof Player)) return;

        final Player player = (Player) event.getAttacker();
        this.tracker.record(player, event);
    }

}
