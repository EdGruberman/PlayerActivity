package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.Tracker;

public final class PlayerToggleSneakEventListener extends EventListener {

    public static final String REFERENCE = "PlayerToggleSneakEvent";

    public PlayerToggleSneakEventListener(final Tracker tracker) {
        super(tracker);
        super.register(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final PlayerToggleSneakEvent event) {
        if (event.isCancelled()) return;

        this.record(event.getPlayer(), System.currentTimeMillis(), event);
    }

}
