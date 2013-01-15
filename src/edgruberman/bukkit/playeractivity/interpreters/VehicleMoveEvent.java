package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class VehicleMoveEvent extends Interpreter {

    public VehicleMoveEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.vehicle.VehicleMoveEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.vehicle.VehicleMoveEvent sub = (org.bukkit.event.vehicle.VehicleMoveEvent) event;
        if (!(sub.getVehicle().getPassenger() instanceof Player)) return;

        this.record((Player) sub.getVehicle().getPassenger(), event);
    }

}
