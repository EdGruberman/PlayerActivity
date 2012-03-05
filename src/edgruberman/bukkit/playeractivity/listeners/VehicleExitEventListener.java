package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleExitEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.EventTracker;

public class VehicleExitEventListener extends EventListener {

    public VehicleExitEventListener(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final VehicleExitEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getExited() instanceof Player)) return;

        final Player player = (Player) event.getExited();
        this.tracker.record(player, event);
    }

}
