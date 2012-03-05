package edgruberman.bukkit.playeractivity;

import org.bukkit.event.Listener;

public abstract class EventListener implements Listener {

    protected final EventTracker tracker;

    protected EventListener(final EventTracker tracker) {
        this.tracker = tracker;
        this.tracker.getPlugin().getServer().getPluginManager().registerEvents(this, this.tracker.getPlugin());
    }

}
