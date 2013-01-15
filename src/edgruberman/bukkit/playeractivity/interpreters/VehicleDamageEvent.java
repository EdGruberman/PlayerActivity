package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class VehicleDamageEvent extends Interpreter {

    public VehicleDamageEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.vehicle.VehicleDamageEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.vehicle.VehicleDamageEvent sub = (org.bukkit.event.vehicle.VehicleDamageEvent) event;
        if (!(sub.getAttacker() instanceof Player)) return;

        this.record((Player) sub.getAttacker(), event);
    }

}
