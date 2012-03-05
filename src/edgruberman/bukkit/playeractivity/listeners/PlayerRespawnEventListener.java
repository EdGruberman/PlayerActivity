package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.EventTracker;

public class PlayerRespawnEventListener extends EventListener {

    public PlayerRespawnEventListener(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final PlayerRespawnEvent event) {
        this.tracker.record(event.getPlayer(), event);
    }

}
