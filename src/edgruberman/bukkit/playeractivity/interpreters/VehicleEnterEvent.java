package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class VehicleEnterEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.vehicle.VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;

        this.record((Player) event.getEntered(), event);
    }

}
