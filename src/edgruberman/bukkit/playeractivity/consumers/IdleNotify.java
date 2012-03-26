package edgruberman.bukkit.playeractivity.consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.ActivityPublisher;
import edgruberman.bukkit.playeractivity.EventTracker;
import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Message;
import edgruberman.bukkit.playeractivity.PlayerActivity;
import edgruberman.bukkit.playeractivity.PlayerIdle;

/**
 * Warn and/or kick players for being idle.
 */
public final class IdleNotify implements Observer {

    public long idle = -1;
    public String privateFormat = null;
    public String broadcast = null;
    public String backBroadcast = null;
    public boolean awayBroadcastOverride = true;

    public final EventTracker tracker;
    private final String ignore;

    private final Plugin plugin;

    public IdleNotify(final Plugin plugin) {
        this.plugin = plugin;
        this.tracker = new EventTracker(plugin);
        this.ignore = plugin.getDescription().getName().toLowerCase() + ".idle.ignore.notify";
    }

    public boolean start(final List<Class<? extends Interpreter>> interpreters) {
        if ((this.idle <= 0) || interpreters.size() == 0) return false;

        this.tracker.idlePublisher.setThreshold(this.idle);
        this.tracker.idlePublisher.addObserver(this);
        this.tracker.activityPublisher.addObserver(this);
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
        // Back
        if (o instanceof ActivityPublisher) {
            final PlayerActivity activity = (PlayerActivity) arg;
            if (activity.last == null || (activity.occurred - activity.last) < this.idle || this.backBroadcast == null || activity.player.hasPermission(this.ignore))
                return;

            if (Main.awayBack != null && this.awayBroadcastOverride && Main.awayBack.isAway(activity.player))
                return;

            final String kickIdle = (Main.idleKick != null ? Main.duration(Main.idleKick.idle) : null);
            Message.manager.broadcast(String.format(this.backBroadcast, Main.duration((activity.occurred - activity.last)), kickIdle, activity.player.getDisplayName()), MessageLevel.EVENT);
            return;
        }

        // Idle
        final PlayerIdle idle = (PlayerIdle) arg;
        if (idle.player.hasPermission(this.ignore)) return;

        if (this.broadcast != null && (Main.awayBack == null || !this.awayBroadcastOverride || !Main.awayBack.isAway(idle.player))) {
            final String kickIdle = (Main.idleKick != null ? Main.duration(Main.idleKick.idle) : null);
            final String messageBroadcast = String.format(this.broadcast, Main.duration(idle.duration), kickIdle, idle.player.getDisplayName());
            Message.manager.broadcast(messageBroadcast, MessageLevel.EVENT);
        }

        if (this.privateFormat != null) {
            final String kickIdle = (Main.idleKick != null ? Main.duration(Main.idleKick.idle) : null);
            final String messagePrivate = String.format(this.privateFormat, Main.duration(idle.duration), kickIdle);
            Message.manager.send(idle.player, messagePrivate, MessageLevel.WARNING);
        }

        return;
    }

}
