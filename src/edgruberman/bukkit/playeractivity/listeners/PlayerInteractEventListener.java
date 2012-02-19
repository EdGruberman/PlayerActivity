package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.Tracker;

public final class PlayerInteractEventListener extends EventListener {

    public static final String REFERENCE = "PlayerInteractEvent";

    public PlayerInteractEventListener(final Tracker tracker) {
        super(tracker);
        super.register(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final PlayerInteractEvent event) {
        // TODO - use event.isCancelled() when bug is fixed that doesn't check right clicking on air with item returning true
        if (event.useInteractedBlock() == Result.DENY && event.useItemInHand() == Result.DENY) return;

        this.record(event.getPlayer(), System.currentTimeMillis(), event);
    }

}
