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
import edgruberman.bukkit.playeractivity.IdlePublisher;
import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.PlayerActivity;
import edgruberman.bukkit.playeractivity.PlayerIdle;

/**
 * Warn and/or kick players for being idle.
 */
public final class IdleKick implements Observer {

    public long warnIdle = -1;
    public String warnPrivate = null;
    public String warnBroadcast = null;
    public String backBroadcast = null;
    public boolean awayBroadcastOverride = true;

    public long kickIdle = -1;
    public String kickReason = null;

    public final EventTracker warn;
    public final EventTracker kick;
    private final String ignore;

    private final Plugin plugin;

    public IdleKick(final Plugin plugin) {
        this.plugin = plugin;
        this.warn = new EventTracker(plugin);
        this.kick = new EventTracker(plugin);
        this.ignore = plugin.getDescription().getName().toLowerCase() + ".idlekick.ignore";
    }

    public boolean start(final List<Class<? extends Interpreter>> interpreters) {
        if ((this.warnIdle <= 0 && this.kickIdle <= 0) || interpreters.size() == 0) return false;

        if (this.warnIdle > 0) {
            this.warn.idlePublisher.setThreshold(this.warnIdle);
            this.warn.idlePublisher.addObserver(this);
            this.warn.activityPublisher.addObserver(this);
            final List<Interpreter> instances = new ArrayList<Interpreter>();
            for (final Class<? extends Interpreter> iClass : interpreters)
                try {
                    instances.add(iClass.newInstance());
                } catch (final Exception e) {
                    this.plugin.getLogger().log(Level.WARNING, "Unable to create activity interpreter: " + iClass.getName(), e);
                }
            this.warn.addInterpreters(instances);
        }
        if (this.kickIdle > 0) {
            this.kick.idlePublisher.setThreshold(this.kickIdle);
            this.kick.idlePublisher.addObserver(this);
            final List<Interpreter> instances = new ArrayList<Interpreter>();
            for (final Class<? extends Interpreter> iClass : interpreters)
                try {
                    instances.add(iClass.newInstance());
                } catch (final Exception e) {
                    this.plugin.getLogger().log(Level.WARNING, "Unable to create activity interpreter: " + iClass.getName(), e);
                }
            this.kick.addInterpreters(instances);
        }
        return true;
    }

    public void stop() {
        this.warn.clear();
        this.kick.clear();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        // Back
        if (o instanceof ActivityPublisher) {
            final PlayerActivity activity = (PlayerActivity) arg;
            if (activity.last == null || (activity.occurred - activity.last) < this.warnIdle || this.backBroadcast == null || activity.player.hasPermission(this.ignore))
                return;

            if (Main.awayBack != null && this.awayBroadcastOverride && Main.awayBack.isAway(activity.player))
                return;

            Main.messageManager.broadcast(String.format(this.backBroadcast, Main.duration((activity.occurred - activity.last)), Main.duration(this.kickIdle), activity.player.getDisplayName()), MessageLevel.EVENT);
            return;
        }

        // IdlePublisher is the only other Observable that sends updates here
        final PlayerIdle idle = (PlayerIdle) arg;
        if (idle.player.hasPermission(this.ignore)) return;

        // Warn
        if (((IdlePublisher) o).getThreshold() == this.warnIdle) {
            if (this.warnBroadcast != null && (Main.awayBack == null || !this.awayBroadcastOverride || !Main.awayBack.isAway(idle.player))) {
                final String messageBroadcast = String.format(this.warnBroadcast, Main.duration(idle.duration), Main.duration(this.kickIdle), idle.player.getDisplayName());
                Main.messageManager.broadcast(messageBroadcast, MessageLevel.EVENT);
            }
            if (this.warnPrivate != null) {
                final String messagePrivate = String.format(this.warnPrivate, Main.duration(idle.duration), Main.duration(this.kickIdle));
                Main.messageManager.send(idle.player, messagePrivate, MessageLevel.WARNING);
            }
            return;
        }

        // Kick
        if (((IdlePublisher) o).getThreshold() == this.kickIdle) {
            final String message = (this.kickReason != null ? String.format(this.kickReason, Main.duration(this.kickIdle)) : null);
            idle.player.kickPlayer(message);
        }
    }

}
