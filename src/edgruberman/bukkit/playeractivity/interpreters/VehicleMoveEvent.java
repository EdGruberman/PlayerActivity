package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class VehicleMoveEvent extends Interpreter {

    public VehicleMoveEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.vehicle.VehicleMoveEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.vehicle.VehicleMoveEvent sub = (org.bukkit.event.vehicle.VehicleMoveEvent) event;
        if (!(sub.getVehicle().getPassenger() instanceof Player)) return;

        this.record((Player) sub.getVehicle().getPassenger(), event);
    }

}
