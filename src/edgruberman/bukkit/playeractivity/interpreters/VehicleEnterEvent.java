package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class VehicleEnterEvent extends Interpreter {

    public VehicleEnterEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.vehicle.VehicleEnterEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.vehicle.VehicleEnterEvent sub = (org.bukkit.event.vehicle.VehicleEnterEvent) event;
        if (!(sub.getEntered() instanceof Player)) return;

        this.record((Player) sub.getEntered(), event);
    }

}
