package edgruberman.bukkit.playeractivity.consumers;

import java.util.Observable;
import java.util.Observer;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.ActivityPublisher;
import edgruberman.bukkit.playeractivity.EventTracker;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Messenger;
import edgruberman.bukkit.playeractivity.PlayerActivity;
import edgruberman.bukkit.playeractivity.PlayerIdle;

/** Notify when a player goes idle */
public final class IdleNotify implements Observer {

    public final long idle;
    public final EventTracker tracker;
    public AwayBack awayBack = null;
    public IdleKick idleKick = null;

    private final Messenger messenger;
    private final String ignore;

    public IdleNotify(final Plugin plugin, final ConfigurationSection config, final Messenger messenger, final String ignore) {
        this.messenger = messenger;
        this.ignore = ignore;
        this.idle = (long) config.getInt("idle", (int) this.idle / 1000) * 1000;

        this.tracker = new EventTracker(plugin);
        for (final String className : config.getStringList("activity"))
            try {
                this.tracker.addInterpreter(EventTracker.newInterpreter(className));
            } catch (final Exception e) {
                plugin.getLogger().warning("Unable to create interpreter for IdleNotify activity: " + className + "; " + e.getClass().getName() + "; " + e.getMessage());
            }

        this.tracker.activityPublisher.addObserver(this);
        this.tracker.idlePublisher.setThreshold(this.idle);
        this.tracker.idlePublisher.addObserver(this);
    }

    public void unload() {
        this.tracker.clear();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        // Back
        if (o instanceof ActivityPublisher) {
            final PlayerActivity activity = (PlayerActivity) arg;
            if (activity.last == null || (activity.occurred - activity.last) < this.idle) return;

            if (this.isAwayOverriding(activity.player) || activity.player.hasPermission(this.ignore)) return;

            final String kickIdle = (this.idleKick != null ? Main.readableDuration(this.idleKick.idle) : null);
            this.messenger.broadcast("idleBackBroadcast", Main.readableDuration((activity.occurred - activity.last)), kickIdle, activity.player.getDisplayName());
            return;
        }

        // Idle
        final PlayerIdle idle = (PlayerIdle) arg;
        if (idle.player.hasPermission(this.ignore)) return;

        if (!this.isAwayOverriding(idle.player)) {
            final String kickIdle = (this.idleKick != null ? Main.readableDuration(this.idleKick.idle) : null);
            this.messenger.broadcast("idleBroadcast", Main.readableDuration(idle.duration), kickIdle, idle.player.getDisplayName());
        }

        final String kickIdle = (this.idleKick != null ? Main.readableDuration(this.idleKick.idle) : null);
        this.messenger.tell(idle.player, "idleNotify", Main.readableDuration(idle.duration), kickIdle);

        return;
    }

    private boolean isAwayOverriding(final Player player) {
        if (this.awayBack == null) return false;

        if (!this.awayBack.overrideIdle) return false;

        return this.awayBack.isAway(player);
    }

}
