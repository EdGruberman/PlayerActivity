package edgruberman.bukkit.playeractivity;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class EventFilter implements Listener {

    public final EventTracker tracker;

    protected EventFilter(final EventTracker tracker) {
        this.tracker = tracker;
    }

    public void register() {
        this.tracker.getPlugin().getServer().getPluginManager().registerEvents(this, this.tracker.getPlugin());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
