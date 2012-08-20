package edgruberman.bukkit.playeractivity.util;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

/** automatic event management based on listeners existing */
public abstract class DynamicEventGenerator extends HandlerList {

    /** called after first listener registers */
    protected abstract void start();

    /** called after last listener unregisters */
    protected abstract void stop();

    @Override
    public synchronized void register(final RegisteredListener listener) {
        super.register(listener);
        if (this.getRegisteredListeners().length != 1) return;

        // start when first listener is registered
        this.start();
    }

    @Override
    public synchronized void unregister(final RegisteredListener listener) {
        final int before = this.getRegisteredListeners().length;
        super.unregister(listener);
        if (this.getRegisteredListeners().length == 0 && before > 0) this.stop();
    }

    @Override
    public synchronized void unregister(final Plugin plugin) {
        final int before = this.getRegisteredListeners().length;
        super.unregister(plugin);
        if (this.getRegisteredListeners().length == 0 && before > 0) this.stop();
    }

    @Override
    public synchronized void unregister(final Listener listener) {
        final int before = this.getRegisteredListeners().length;
        super.unregister(listener);
        if (this.getRegisteredListeners().length == 0 && before > 0) this.stop();
    }

}
