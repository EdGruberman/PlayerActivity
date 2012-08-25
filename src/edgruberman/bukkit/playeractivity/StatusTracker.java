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

import edgruberman.bukkit.playeractivity.interpreters.Interpreter;

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

    public Interpreter addInterpreter(final String className) throws ClassCastException, ClassNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Class<? extends Interpreter> clazz = Interpreter.find(className);
        for (final Interpreter interpreter : this.interpreters)
            if (interpreter.getClass().equals(clazz))
                throw new IllegalStateException("Duplicate Interpreter class assignment: " + interpreter.getClass());

        final Interpreter interpreter = clazz.getConstructor(StatusTracker.class).newInstance(this);
        this.interpreters.add(interpreter);
        return interpreter;
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

    public long getIdleThreshold() {
        return this.idlePublisher.threshold;
    }

    public List<Player> getIdle() {
        return this.idlePublisher.idle;
    }

}
