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

public final class EventTracker extends Observable implements Listener {

    private final Plugin plugin;
    private final Map<Player, Long> last = new HashMap<Player, Long>();
    private final List<Interpreter> interpreters = new ArrayList<Interpreter>();

    public EventTracker(final Plugin plugin) {
        this(plugin, Collections.<Interpreter>emptyList());
    }

    public EventTracker(final Plugin plugin, final List<Interpreter> interpreters) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.addInterpreters(interpreters);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public void addInterpreters(final List<Interpreter> interpreters) {
        for (final Interpreter interpreter : interpreters) this.addInterpreter(interpreter);
    }

    public boolean addInterpreter(final Interpreter interpreter) {
        this.interpreters.add(interpreter);
        interpreter.register(this);
        return true;
    }

    public List<Interpreter> getInterpreters() {
        return this.interpreters;
    }

    public void clear() {
        for (final Interpreter filter : this.interpreters) filter.unregister();
        this.interpreters.clear();
        this.last.clear();
    }

    public Map<Player, Long> getLastAll() {
        return this.last;
    }

    public Long getLastFor(final Player player) {
        return this.last.get(player);
    }

    /**
     * Record last activity for player.
     * (This could be called on high frequency events such as PLAYER_MOVE.)
     *
     * @param player player to record this as last activity for
     * @param type event type that player engaged in
     */
    public void record(final Player player, final Event event) {
        final long occured = System.currentTimeMillis();

        this.last.put(player, occured);

        this.setChanged();
        if (this.countObservers() == 0) return;

        this.notifyObservers(new PlayerEvent(player, event, occured));
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.last.remove(event.getPlayer());
    }

    // TODO gracefully manage a NoClassDefFoundError when initializing an existing Interpreter SubClass importing a class not found
    public static Interpreter newInterpreter(final String className) {
        Class<? extends Interpreter> subClass = null;

        // Look in local package
        try {
            subClass = Class.forName("edgruberman.bukkit.playeractivity.filters." + className).asSubclass(Interpreter.class);
        } catch (final Exception e) {
            // Ignore
        }

        // Look for a custom class
        if (subClass == null) {
            try {
                subClass = Class.forName(className).asSubclass(Interpreter.class);
            } catch (final Exception e) {
                return null;
            }
        }

        // Instantiate class
        Interpreter interpreter;
        try {
            interpreter = subClass.newInstance();
        } catch (final Exception e) {
            return null;
        }

        return interpreter;
    }

}
