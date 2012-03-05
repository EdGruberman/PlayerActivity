package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;

public final class EventTracker extends Observable implements Listener {

    private final Plugin plugin;
    private final Map<Player, Long> last = new HashMap<Player, Long>();
    private final List<EventFilter> filters = new ArrayList<EventFilter>();

    public EventTracker(final Plugin plugin) {
        this(plugin, Collections.<Class<? extends EventFilter>>emptyList());
    }

    public EventTracker(final Plugin plugin, final List<Class<? extends EventFilter>> filters) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.addFilters(filters);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public void addFilters(final List<Class<? extends EventFilter>> filters) {
        for (final Class<? extends EventFilter> filter : filters) this.addFilter(filter);
    }

    public boolean addFilter(final Class<? extends EventFilter> filter) {
        EventFilter instance = null;
        try {
            instance = filter.getConstructor(EventTracker.class).newInstance(this);
        } catch (final Exception e) {
            Main.messageManager.log("Error instantiating EventListener " + filter.getName(), MessageLevel.SEVERE, e);
            return false;
        }

        this.filters.add(instance);
        instance.register();
        return true;
    }

    public void clearFilters() {
        for (final EventFilter filter : this.filters) filter.unregister();
        this.filters.clear();
    }

    public Map<Player, Long> getLastAll() {
        return this.last;
    }

    public Long getLastFor(final Player player) {
        return this.last.get(player);
    }

    /**
     * Record last activity for player assuming current time for occurrence.
     * (This could be called on high frequency events such as PLAYER_MOVE.)
     *
     * @param player player to record this as last activity for
     * @param type event type that player engaged in
     */
    public void record(final Player player, final Event event) {
            this.record(player, event, System.currentTimeMillis());
    }

    /**
     * Record last activity for player.
     * (This could be called on high frequency events such as PLAYER_MOVE.)
     *
     * @param player player to record this as last activity for
     * @param type event type that player engaged in (TODO store in Status for later reference based on Tracker field indicating to keep reference)
     * @param occurred milliSeconds from midnight, January 1, 1970 UTC the activity was performed at
     */
    public void record(final Player player, final Event event, final long occurred) {
        this.last.put(player, occurred);

        if (this.countObservers() == 0) return;

        this.notifyObservers(new PlayerEvent(player, event, occurred));
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.last.remove(event.getPlayer());
    }

}
