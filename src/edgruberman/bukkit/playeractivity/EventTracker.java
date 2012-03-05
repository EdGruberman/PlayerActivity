package edgruberman.bukkit.playeractivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;

public final class EventTracker implements Listener {

    private final Plugin plugin;
    private final Map<Player, PlayerEvent> last = new HashMap<Player, PlayerEvent>();

    public EventTracker(final Plugin plugin, final List<Class<? extends EventListener>> listeners) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        for (final Class<? extends EventListener> listener : listeners) this.addListener(listener);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public boolean addListener(final Class<? extends EventListener> listener) {
        try {
            listener.getConstructor(EventTracker.class).newInstance(this);
        } catch (final Exception e) {
            Main.messageManager.log("Error instantiating EventListener " + listener.getName(), MessageLevel.SEVERE, e);
            return false;
        }

        return true;
    }

    public Map<Player, PlayerEvent> getLastAll() {
        return this.last;
    }

    public PlayerEvent getLastFor(final Player player) {
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
        if (!this.last.containsKey(player)) {
            this.last.put(player, new PlayerEvent(player, occurred));
            return;
        }

        final PlayerEvent status = this.last.get(player);
        status.setOccurred(occurred);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.last.remove(event.getPlayer());
    }

}
