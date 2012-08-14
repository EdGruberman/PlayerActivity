package edgruberman.bukkit.playeractivity.consumers;

import java.util.Observable;
import java.util.Observer;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.ActivityPublisher;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.PlayerActive;
import edgruberman.bukkit.playeractivity.PlayerIdle;
import edgruberman.bukkit.playeractivity.StatusTracker;
import edgruberman.bukkit.playeractivity.interpreters.Interpreter;
import edgruberman.bukkit.playeractivity.messaging.couriers.ConfigurationCourier;

/** notify when a player goes idle */
public final class IdleNotify implements Observer {

    public final long idle;
    public final StatusTracker tracker;
    public AwayBack awayBack = null;
    public IdleKick idleKick = null;

    private final ConfigurationCourier courier;
    private final String ignore;

    public IdleNotify(final Plugin plugin, final ConfigurationSection config, final ConfigurationCourier courier, final String ignore) {
        this.courier = courier;
        this.ignore = ignore;
        this.idle = (long) config.getInt("idle", (int) this.idle / 1000) * 1000;

        this.tracker = new StatusTracker(plugin);
        for (final String className : config.getStringList("activity"))
            try {
                this.tracker.addInterpreter(Interpreter.create(className));
            } catch (final Exception e) {
                plugin.getLogger().warning("Unable to create interpreter for IdleNotify activity: " + className + "; " + e.getClass().getName() + "; " + e.getMessage());
            }

        this.tracker.register(this, PlayerActive.class);
        this.tracker.setIdleThreshold(this.idle);
        this.tracker.register(this, PlayerIdle.class);
    }

    public void unload() {
        this.tracker.clear();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        // Back
        if (o instanceof ActivityPublisher) {
            final PlayerActive activity = (PlayerActive) arg;
            if (activity.last == null || (activity.occurred - activity.last) < this.idle) return;

            if (this.isAwayOverriding(activity.player) || activity.player.hasPermission(this.ignore)) return;

            final String kickIdle = (this.idleKick != null ? Main.readableDuration(this.idleKick.idle) : null);
            this.courier.broadcast("idleBackBroadcast", Main.readableDuration((activity.occurred - activity.last)), kickIdle, activity.player.getDisplayName());
            return;
        }

        // Idle
        final PlayerIdle idle = (PlayerIdle) arg;
        if (idle.player.hasPermission(this.ignore)) return;

        if (!this.isAwayOverriding(idle.player)) {
            final String kickIdle = (this.idleKick != null ? Main.readableDuration(this.idleKick.idle) : null);
            this.courier.broadcast("idleBroadcast", Main.readableDuration(idle.duration), kickIdle, idle.player.getDisplayName());
        }

        final String kickIdle = (this.idleKick != null ? Main.readableDuration(this.idleKick.idle) : null);
        this.courier.send(idle.player, "idleNotify", Main.readableDuration(idle.duration), kickIdle);

        return;
    }

    private boolean isAwayOverriding(final Player player) {
        if (this.awayBack == null) return false;

        if (!this.awayBack.overrideIdle) return false;

        return this.awayBack.isAway(player);
    }

}
