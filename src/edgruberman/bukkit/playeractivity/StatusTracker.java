package edgruberman.bukkit.playeractivity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.interpreters.Interpreter;

public final class StatusTracker implements Listener {

    final Plugin plugin;
    final Set<Interpreter> interpreters = new HashSet<Interpreter>();
    final ActivityPublisher activityPublisher = new ActivityPublisher();
    final IdlePublisher idlePublisher = new IdlePublisher(this);

    public StatusTracker(final Plugin plugin) {
        this(plugin, Collections.<Interpreter>emptyList());
    }

    public StatusTracker(final Plugin plugin, final List<Interpreter> interpreters) {
        this.plugin = plugin;
        for (final Interpreter interpreter : interpreters) this.addInterpreter(interpreter);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void clear() {
        HandlerList.unregisterAll(this);
        for (final Interpreter interpreter : this.interpreters) interpreter.clear();
        this.interpreters.clear();
        this.activityPublisher.clear();
        this.idlePublisher.clear();
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public boolean addInterpreter(final Interpreter interpreter) {
        if (!this.interpreters.add(interpreter)) {
            this.plugin.getLogger().warning("Duplicate interpreter specified for: " + interpreter.getClass());
            return false;
        }

        interpreter.register(this);
        return true;
    }

    public Set<Interpreter> getInterpreters() {
        return Collections.unmodifiableSet(this.interpreters);
    }

    public Observable register(final Observer observer, final Class<? extends PlayerStatus> status) {
        if (status.equals(PlayerActive.class)) {
            this.activityPublisher.addObserver(observer);
            return this.activityPublisher;

        } else if (status.equals(PlayerIdle.class)) {
            this.idlePublisher.addObserver(observer);
            return this.idlePublisher;
        }

        throw new IllegalArgumentException("Unsupported PlayerStatus class: " + status.getName());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.activityPublisher.last.remove(event.getPlayer());
        this.idlePublisher.idle.remove(event.getPlayer());
    }



    // ---- Active ----

    /**
     * record activity for player
     *
     * @param player player to record this as last activity for
     * @param event event player engaged in
     */
    public void record(final Player player, final Class<? extends Event> event) {
        this.activityPublisher.record(player, event);
    }

    public Map<Player, Long> getLastAll() {
        return this.activityPublisher.last;
    }

    public Long getLastFor(final Player player) {
        return this.activityPublisher.last.get(player);
    }



    // ---- Idle ----

    /** @param threshold duration in milliseconds at which a player is considered idle if no activity */
    public void setIdleThreshold(final long threshold) {
        this.idlePublisher.threshold = (threshold <= 0 ? -1 : threshold);
        // TODO update any existing timers with new threshold time (update for those exceeding, extend for those under)
    }

    public long getIdleThreshold() {
        return this.idlePublisher.threshold;
    }

    public List<Player> getIdle() {
        return this.idlePublisher.idle;
    }

}
