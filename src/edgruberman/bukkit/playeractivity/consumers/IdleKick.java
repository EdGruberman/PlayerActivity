package edgruberman.bukkit.playeractivity.consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.EventTracker;
import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.PlayerIdle;

/**
 * Warn and/or kick players for being idle.
 */
public final class IdleKick implements Observer {

    public long idle = -1;
    public String reason = null;

    public final EventTracker tracker;
    private final String ignore;

    private final Plugin plugin;

    public IdleKick(final Plugin plugin) {
        this.plugin = plugin;
        this.tracker = new EventTracker(plugin);
        this.ignore = plugin.getDescription().getName().toLowerCase() + ".idle.ignore.kick";
    }

    public boolean start(final List<Class<? extends Interpreter>> interpreters) {
        if ((this.idle <= 0) || interpreters.size() == 0) return false;

        this.tracker.idlePublisher.setThreshold(this.idle);
        this.tracker.idlePublisher.addObserver(this);
        final List<Interpreter> instances = new ArrayList<Interpreter>();
        for (final Class<? extends Interpreter> iClass : interpreters)
            try {
                instances.add(iClass.newInstance());
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "Unable to create activity interpreter: " + iClass.getName(), e);
            }
        this.tracker.addInterpreters(instances);
        return true;
    }

    public void stop() {
        this.tracker.clear();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final PlayerIdle idle = (PlayerIdle) arg;
        if (idle.player.hasPermission(this.ignore)) return;

        final String message = (this.reason != null ? String.format(this.reason, Main.duration(this.idle)) : null);
        idle.player.kickPlayer(message);
    }

}
