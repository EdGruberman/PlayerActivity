package edgruberman.bukkit.playeractivity;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

/** interprets the player involved in an event */
public abstract class Interpreter implements Listener, EventExecutor {

    public static Interpreter create(final String className, final StatusTracker tracker)
            throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException
                , InvocationTargetException, NoSuchMethodException, ClassCastException, ClassNotFoundException {
        return Interpreter
                .find(className)
                .getConstructor(StatusTracker.class)
                .newInstance(tracker);
    }

    public static Class<? extends Interpreter> find(final String className) throws ClassNotFoundException, ClassCastException {
        try {
            return Class.forName(Interpreter.class.getPackage().getName() + ".interpreters." + className).asSubclass(Interpreter.class);
        } catch (final Exception e) {
            return Class.forName(className).asSubclass(Interpreter.class);
        }
    }



    // ---- instance ----

    protected final StatusTracker tracker;
    protected final Class<? extends Event> type;

    protected Interpreter(final StatusTracker tracker, final Class<? extends Event> type) {
        this.tracker = tracker;
        this.type = type;
        Bukkit.getPluginManager().registerEvent(this.type, this, this.getEventPriority(), this, this.tracker.getPlugin(), this.getIgnoreCancelled());
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        if (this.type.isAssignableFrom(event.getClass())) this.onExecute(event);
    }

    protected abstract void onExecute(Event event);

    protected void record(final Player player, final Event event) {
        if (event.isAsynchronous()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.tracker.getPlugin(), new SynchronizedEventRecorder(player, event.getClass()));
            return;
        }

        this.tracker.record(player, event.getClass());
    }

    public void clear() {
        HandlerList.unregisterAll(this);
    }

    public EventPriority getEventPriority() {
        return EventPriority.LOW;
    }

    public boolean getIgnoreCancelled() {
        return true;
    }



    // ---- synchronized recording of asynchronous events ----

    public class SynchronizedEventRecorder implements Runnable {

        private final Player player;
        private final Class<? extends Event> event;

        private SynchronizedEventRecorder(final Player player, final Class<? extends Event> event) {
            this.player = player;
            this.event = event;
        }

        @Override
        public void run() {
            Interpreter.this.tracker.record(this.player, this.event);
        }

    }

}
