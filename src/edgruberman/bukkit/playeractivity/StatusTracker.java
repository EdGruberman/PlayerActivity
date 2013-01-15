package edgruberman.bukkit.playeractivity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public final class StatusTracker implements Listener {

    final Plugin plugin;
    final List<Interpreter> interpreters = new ArrayList<Interpreter>();
    final ActivityPublisher activityPublisher;
    final IdlePublisher idlePublisher;

    public StatusTracker(final Plugin plugin) {
        this(plugin, -1);
    }

    public StatusTracker(final Plugin plugin, final long idle) {
        this.plugin = plugin;

        this.activityPublisher = new ActivityPublisher();
        this.idlePublisher = new IdlePublisher(plugin, this.activityPublisher, idle);

        // populate last activity and start idle timer in case no activity from player after initialization
        for (final Player player : Bukkit.getServer().getOnlinePlayers())
            this.activityPublisher.record(player, PlayerEvent.class);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void clear() {
        HandlerList.unregisterAll(this);
        for (final Interpreter interpreter : this.interpreters) interpreter.clear();
        this.interpreters.clear();
        this.idlePublisher.clear();
        this.activityPublisher.clear();
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    /** @throws IllegalStateException when existing Interpreter class already was added */
    public void addInterpreter(final Interpreter i) throws IllegalStateException {
        for (final Interpreter existing : this.interpreters)
            if (existing.getClass().equals(i.getClass()))
                throw new IllegalStateException("Duplicate Interpreter class assignment: " + existing.getClass());

        this.interpreters.add(i);
    }

    public Interpreter addInterpreter(final String className) throws IllegalArgumentException, SecurityException, ClassCastException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        final Interpreter i = Interpreter.create(className, this);
        this.addInterpreter(i);
        return i;
    }

    public List<Interpreter> getInterpreters() {
        return Collections.unmodifiableList(this.interpreters);
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
        this.activityPublisher.last.remove(event.getPlayer().getName());
        this.idlePublisher.idle.remove(event.getPlayer().getName());
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

    public Map<String, Long> getLast() {
        return this.activityPublisher.last;
    }

    public Long getLastFor(final Player player) {
        return this.activityPublisher.last.get(player.getName());
    }



    // ---- Idle ----

    public long getIdleThreshold() {
        return this.idlePublisher.threshold;
    }

    public List<String> getIdle() {
        return this.idlePublisher.idle;
    }

}
