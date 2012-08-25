package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.StatusTracker;

/** interprets the player involved in an event */
public class Interpreter implements Listener {

    public static Class<? extends Interpreter> find(final String className) throws ClassNotFoundException, ClassCastException {
        // Look in local package
        try {
            return Class.forName(Interpreter.class.getPackage().getName() + "." + className).asSubclass(Interpreter.class);
        } catch (final Exception e) {
            // Ignore to try searching for custom class next
        }

        // Look for a custom class
        return Class.forName(className).asSubclass(Interpreter.class);
    }



    // ---- instance ----

    protected StatusTracker tracker;

    protected Interpreter(final StatusTracker tracker) {
        this.tracker = tracker;
        Bukkit.getPluginManager().registerEvents(this, this.tracker.getPlugin());
    }

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



    // ---- Utility Class ----

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
