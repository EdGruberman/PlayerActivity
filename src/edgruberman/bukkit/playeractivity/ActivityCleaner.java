package edgruberman.bukkit.playeractivity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

final class ActivityCleaner implements Listener {

    private final EventTracker tracker;

    ActivityCleaner(final EventTracker tracker) {
        this.tracker = tracker;
    }

    void start() {
        this.tracker.getPlugin().getServer().getPluginManager().registerEvents(this, this.tracker.getPlugin());
    }

    void stop() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.tracker.activityPublisher.last.remove(event.getPlayer());
        this.tracker.idlePublisher.remove(event.getPlayer());
    }

}
