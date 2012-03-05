package edgruberman.bukkit.playeractivity;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Player related Event class wrapper to determine player generating activity and time of activity.
 */
public class Interpreter implements Listener {

    protected EventTracker tracker;

    void register(final EventTracker tracker) {
        this.tracker = tracker;
        tracker.getPlugin().getServer().getPluginManager().registerEvents(this, tracker.getPlugin());
    }

    void unregister() {
        HandlerList.unregisterAll(this);
    }

}
