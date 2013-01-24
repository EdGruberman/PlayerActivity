package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class VehicleExitEvent extends Interpreter {

    public VehicleExitEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.vehicle.VehicleExitEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.vehicle.VehicleExitEvent sub = (org.bukkit.event.vehicle.VehicleExitEvent) event;
        if (!(sub.getExited() instanceof Player)) return;

        this.record((Player) sub.getExited(), event);
    }

}
