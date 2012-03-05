package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.EventFilter;
import edgruberman.bukkit.playeractivity.EventTracker;

public class VehicleEnterEvent extends EventFilter {

    public VehicleEnterEvent(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final org.bukkit.event.vehicle.VehicleEnterEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntered() instanceof Player)) return;

        final Player player = (Player) event.getEntered();
        this.tracker.record(player, event);
    }

}
