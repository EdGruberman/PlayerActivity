package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class VehicleExitEvent extends Interpreter {

    public VehicleExitEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.vehicle.VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) return;

        this.record((Player) event.getExited(), event);
    }

}
