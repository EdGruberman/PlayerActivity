package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAnimationEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.EventTracker;

public class PlayerAnimationEventListener extends EventListener {

    public PlayerAnimationEventListener(final EventTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final PlayerAnimationEvent event) {
        if (event.isCancelled()) return;

        this.tracker.record(event.getPlayer(), event);
    }

}
