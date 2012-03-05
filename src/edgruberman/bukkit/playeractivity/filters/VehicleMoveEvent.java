package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.EventFilter;
import edgruberman.bukkit.playeractivity.EventTracker;

public class VehicleMoveEvent extends EventFilter {

    public VehicleMoveEvent(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final org.bukkit.event.vehicle.VehicleMoveEvent event) {
        if (!(event.getVehicle().getPassenger() instanceof Player)) return;

        final Player player = (Player) event.getVehicle().getPassenger();
        this.tracker.record(player, event);
    }

}
