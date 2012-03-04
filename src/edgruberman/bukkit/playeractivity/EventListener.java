package edgruberman.bukkit.playeractivity;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public abstract class EventListener implements Listener {

    private final Tracker tracker;

    protected EventListener(final Tracker tracker) {
        this.tracker = tracker;
    }

    protected void register(final Listener listener) {
        this.tracker.plugin.getServer().getPluginManager().registerEvents(listener, this.tracker.plugin);
    }

    public void record(final Player player, final Event event) {
        this.tracker.record(player, event);
    }

    public void record(final Player player, final Event event, final long occured) {
        this.tracker.record(player, event, occured);
    }

}
