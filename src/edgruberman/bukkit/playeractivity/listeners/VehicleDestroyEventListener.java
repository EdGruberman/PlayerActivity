package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.EventTracker;

public class VehicleDestroyEventListener extends EventListener {

    public VehicleDestroyEventListener(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final VehicleDestroyEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getAttacker() instanceof Player)) return;

        final Player player = (Player) event.getAttacker();
        this.tracker.record(player, event);
    }

}
