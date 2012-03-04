package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleExitEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.Tracker;

public final class VehicleExitEventListener extends EventListener {

    public VehicleExitEventListener(final Tracker tracker) {
        super(tracker);
        super.register(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final VehicleExitEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getExited() instanceof Player)) return;

        final Player player = (Player) event.getExited();
        this.record(player, event);
    }

}
