package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.EventTracker;

public class VehicleEnterEventListener extends EventListener {

    public VehicleEnterEventListener(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final VehicleEnterEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntered() instanceof Player)) return;

        final Player player = (Player) event.getEntered();
        this.tracker.record(player, event);
    }

}
