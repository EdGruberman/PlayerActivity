package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class VehicleDestroyEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.vehicle.VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) return;

        this.record((Player) event.getAttacker(), event);
    }

}
