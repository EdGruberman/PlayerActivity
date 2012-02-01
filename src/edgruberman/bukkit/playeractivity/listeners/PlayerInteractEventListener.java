package edgruberman.bukkit.playeractivity.listeners;

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
        if (event.isCancelled()) return;

        this.record(event.getPlayer(), System.currentTimeMillis(), event);
    }

}
