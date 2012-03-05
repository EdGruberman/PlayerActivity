package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.EventTracker;

public class PlayerInteractEventListener extends EventListener {

    public PlayerInteractEventListener(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final PlayerInteractEvent event) {
        // TODO - use event.isCancelled() when bug is fixed that doesn't check right clicking on air with item returning true
        if (event.useInteractedBlock() == Result.DENY && event.useItemInHand() == Result.DENY) return;

        this.tracker.record(event.getPlayer(), event);
    }

}
