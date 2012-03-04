package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.Tracker;

public final class VehicleMoveEventListener extends EventListener {

    public VehicleMoveEventListener(final Tracker tracker) {
        super(tracker);
        super.register(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final VehicleMoveEvent event) {
        if (!(event.getVehicle().getPassenger() instanceof Player)) return;

        final Player player = (Player) event.getVehicle().getPassenger();
        this.record(player, event);
    }

}
