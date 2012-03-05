package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.EventTracker;

public class PlayerFishEventListener extends EventListener {

    public PlayerFishEventListener(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final PlayerFishEvent event) {
        if (event.isCancelled()) return;

        this.tracker.record(event.getPlayer(), event);
    }

}
