package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class EventTracker implements Listener {

    public final ActivityPublisher activityPublisher = new ActivityPublisher();
    public final IdlePublisher idlePublisher = new IdlePublisher(this);

    private final Plugin plugin;
    private final List<Interpreter> interpreters = new ArrayList<Interpreter>();
    private EventPriority defaultPriority = EventPriority.MONITOR;
    private boolean defaultIgnoreCancelled = true;
    private final ActivityCleaner cleaner = new ActivityCleaner(this);

    public EventTracker(final Plugin plugin) {
        this(plugin, Collections.<Interpreter>emptyList());
    }

    public EventTracker(final Plugin plugin, final List<Interpreter> interpreters) {
        this.plugin = plugin;
        new ActivityCleaner(this);
        this.addInterpreters(interpreters);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public void addInterpreters(final List<Interpreter> interpreters) {
        for (final Interpreter interpreter : interpreters) this.addInterpreter(interpreter);
    }

    public boolean addInterpreter(final Interpreter interpreter) {
        this.cleaner.start();
        this.interpreters.add(interpreter);
        interpreter.register(this);
        return true;
    }

    public List<Interpreter> getInterpreters() {
        return this.interpreters;
    }

    public void clear() {
        // TODO when BUKKIT-1192 is implemented, move this back into Interpreter, make this EventExecutor and move ActivityCleaner/unregister into here
        HandlerList.unregisterAll(this);
        this.cleaner.stop();

        this.interpreters.clear();
        this.activityPublisher.clear();
        this.idlePublisher.clear();
    }

    public Map<Player, Long> getLastAll() {
        return this.activityPublisher.last;
    }

    public Long getLastFor(final Player player) {
        return this.activityPublisher.last.get(player);
    }

    public EventPriority getDefaultPriority() {
        return this.defaultPriority;
    }

    public boolean isDefaultIgnoreCancelled() {
        return this.defaultIgnoreCancelled;
    }

    public void setDefaultPriority(final EventPriority defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    public void setDefaultIgnoreCancelled(final boolean defaultIgnoreCancelled) {
        this.defaultIgnoreCancelled = defaultIgnoreCancelled;
    }

    // TODO gracefully manage a NoClassDefFoundError when initializing an existing Interpreter subclass importing a class not found
    public static Interpreter newInterpreter(final String className) {
        // Find class
        Class<? extends Interpreter> subClass = null;
        subClass = EventTracker.findInterpreter(className);
        if (subClass == null) return null;

        // Instantiate class
        Interpreter interpreter;
        try {
            interpreter = subClass.newInstance();
        } catch (final Exception e) {
            return null;
        }

        return interpreter;
    }

    public static Class<? extends Interpreter> findInterpreter(final String className) {
        // Look in local package
        try {
            return Class.forName("edgruberman.bukkit.playeractivity.interpreters." + className).asSubclass(Interpreter.class);
        } catch (final Exception e) {
            // Ignore
        }

        // Look for a custom class
        try {
            return Class.forName(className).asSubclass(Interpreter.class);
        } catch (final Exception e) {
            return null;
        }
    }

}
