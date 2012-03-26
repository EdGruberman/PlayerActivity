package edgruberman.bukkit.playeractivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

/**
 * Interprets the player involved in a player related Event and time of activity.
 */
public abstract class Interpreter implements EventExecutor {

    protected EventTracker tracker;
    protected Player player = null;
    protected boolean isCancelled = false;

    private Method eventMethod;
    private Class<? extends Event> eventClass;

    @Override
    public void execute(final Listener listener, final Event event) {
        try {
            this.eventMethod.invoke(this, event);
        } catch (final Exception e) {
            this.tracker.getPlugin().getLogger().log(Level.SEVERE, "Unable to invoke method: " + this.eventMethod.toString(), e);
        }

        if (this.player == null || this.isCancelled) return;

        this.tracker.activityPublisher.record(this.player, event);

        // Reset for next event
        this.player = null;
        this.isCancelled = false;
    }

    /**
     * Dynamically determines the Event the subclass supports and registers it with the PluginManager.
     */
    void register(final EventTracker tracker) {
        this.tracker = tracker;

        for (final Method method : this.getClass().getDeclaredMethods()) {
            // Method must have an EventHandler annotation in order to be registered
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;

            // Method must have a single parameter of something that extends Event
            final Class<?> checkClass = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(checkClass) || method.getParameterTypes().length != 1) new IllegalArgumentException(method.toString());
            this.eventMethod = method;
            this.eventClass = checkClass.asSubclass(Event.class);

            // Default to plugin supplied values, override with explicit annotation values
            EventPriority priority = tracker.getDefaultPriority();
            boolean ignoreCancelled = tracker.isDefaultIgnoreCancelled();
            for (final Field field : eh.getClass().getDeclaredFields()) {
                if (field.getName().equals("priority")) priority = eh.priority();
                if (field.getName().equals("ignoreCancelled")) ignoreCancelled = eh.ignoreCancelled();
            }

            // Register the executor
            tracker.getPlugin().getServer().getPluginManager().registerEvent(this.eventClass, tracker, priority, this, tracker.getPlugin(), ignoreCancelled);

            return; // Only register the first EventHandler method found
        }
    }

// TODO add when BUKKIT-1192 is implemented
//    void unregister() {
//        for (final RegisteredListener listener : HandlerList.getRegisteredListeners(this.tracker.getPlugin()))
//            if (listener.getListener().equals(this.tracker) && listener.getEventExecutor().equals(this)) {
//                Method method;
//                try {
//                    method = this.eventClass.getMethod("getHandlerList");
//                } catch (final Exception e) {
//                    this.tracker.getPlugin().getLogger().log(Level.WARNING, "Unable to find getHandlerList method on: " + this.eventClass.getName(), e);
//                    return;
//                }
//
//                HandlerList handlerList;
//                method.setAccessible(true);
//                try {
//                    handlerList = (HandlerList) method.invoke(this.eventClass);
//                } catch (final Exception e) {
//                    this.tracker.getPlugin().getLogger().log(Level.WARNING, "Unable to retrieve HandlerList for: " + this.eventClass.getName(), e);
//                    return;
//                }
//                handlerList.unregister(listener);
//
//                return;
//            }
//    }

}
