package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.Tracker;

public final class PlayerDropItemEventListener extends EventListener {

    public static final String REFERENCE = "PlayerDropItemEvent";

    public PlayerDropItemEventListener(final Tracker tracker) {
        super(tracker);
        super.register(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final PlayerDropItemEvent event) {
        if (event.isCancelled()) return;

        this.record(event.getPlayer(), System.currentTimeMillis(), event);
    }

}
