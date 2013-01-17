package edgruberman.bukkit.playeractivity.consumers;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.ActivityPublisher;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.PlayerActive;
import edgruberman.bukkit.playeractivity.PlayerIdle;
import edgruberman.bukkit.playeractivity.StatusTracker;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;

/** notify when a player goes idle */
public final class IdleNotify implements Observer {

    public final StatusTracker tracker;

    private final ConfigurationCourier courier;
    private final String track;
    private final boolean cancelWhenAway;
    private final long idleKick;

    public IdleNotify(final Plugin plugin, final long idle, final List<String> activity, final long idleKick, final boolean cancelWhenAway, final ConfigurationCourier courier, final String track) {
        this.courier = courier;
        this.track = track;
        this.cancelWhenAway = cancelWhenAway;
        this.idleKick = idleKick;

        this.tracker = new StatusTracker(plugin, idle);
        for (final String className : activity)
            try {
                this.tracker.addInterpreter(className);
            } catch (final Exception e) {
                plugin.getLogger().warning("Unable to create interpreter for IdleNotify activity: " + className + "; " + e);
            }

        this.tracker.register(this, PlayerActive.class);
        this.tracker.register(this, PlayerIdle.class);
    }

    public void unload() {
        this.tracker.clear();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        // active
        if (o instanceof ActivityPublisher) {
            final PlayerActive activity = (PlayerActive) arg;
            if (activity.last == null || (activity.occurred - activity.last) < this.tracker.getIdleThreshold()) return;

            if (!activity.player.hasPermission(this.track)) return;
            if (this.cancelWhenAway && (this.isAway(activity.player) || activity.event == PlayerBack.class)) return;

            final String kickIdle = (this.idleKick > 0 ? Main.readableDuration(this.idleKick) : null);
            this.courier.broadcast("active", Main.readableDuration((activity.occurred - activity.last)), kickIdle, activity.player.getDisplayName());
            return;
        }

        // idle
        final PlayerIdle idle = (PlayerIdle) arg;
        if (!idle.player.hasPermission(this.track)) return;
        if (this.cancelWhenAway && this.isAway(idle.player)) return;

        final String kickIdle = (this.idleKick > 0 ? Main.readableDuration(this.idleKick) : null);
        this.courier.broadcast("idle", Main.readableDuration(idle.duration), kickIdle, idle.player.getDisplayName());
        this.courier.send(idle.player, "idle-notify", Main.readableDuration(idle.duration), kickIdle);
        return;
    }

    private boolean isAway(final Player player) {
        return ( player.hasMetadata("away") ? player.getMetadata("away").get(0).asBoolean() : false);
    }

}
