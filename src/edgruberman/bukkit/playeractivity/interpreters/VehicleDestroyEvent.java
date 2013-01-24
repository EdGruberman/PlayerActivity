package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class VehicleDestroyEvent extends Interpreter {

    public VehicleDestroyEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.vehicle.VehicleDestroyEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.vehicle.VehicleDestroyEvent sub = (org.bukkit.event.vehicle.VehicleDestroyEvent) event;
        if (!(sub.getAttacker() instanceof Player)) return;

        this.record((Player) sub.getAttacker(), event);
    }

}
