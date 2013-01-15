package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class VehicleExitEvent extends Interpreter {

    public VehicleExitEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.vehicle.VehicleExitEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.vehicle.VehicleExitEvent sub = (org.bukkit.event.vehicle.VehicleExitEvent) event;
        if (!(sub.getExited() instanceof Player)) return;

        this.record((Player) sub.getExited(), event);
    }

}
