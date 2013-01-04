package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class VehicleEnterEvent extends Interpreter {

    public VehicleEnterEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.vehicle.VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;

        this.record((Player) event.getEntered(), event);
    }

}
