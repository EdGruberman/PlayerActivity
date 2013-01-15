package edgruberman.bukkit.playeractivity.consumers;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.PlayerIdle;
import edgruberman.bukkit.playeractivity.StatusTracker;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;

/** kick players for being idle */
public final class IdleKick implements Observer {

    public final StatusTracker tracker;

    private final ConfigurationCourier courier;
    private final String ignore;

    public IdleKick(final Plugin plugin, final long idle, final List<String> activity, final ConfigurationCourier courier, final String ignore) {
        this.courier = courier;
        this.ignore = ignore;

        this.tracker = new StatusTracker(plugin, idle);
        for (final String className : activity)
            try {
                this.tracker.addInterpreter(className);
            } catch (final Exception e) {
                plugin.getLogger().warning("Unable to create interpreter for IdleKick activity: " + className + "; " + e);
            }

        this.tracker.register(this, PlayerIdle.class);
    }

    public void unload() {
        this.tracker.clear();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final PlayerIdle idle = (PlayerIdle) arg;
        if (idle.player.hasPermission(this.ignore)) return;

        final String reason = this.courier.format("+idle-kick-reason");
        final String message = (reason != null ? String.format(reason, Main.readableDuration(this.tracker.getIdleThreshold())) : null);
        idle.player.kickPlayer(message);
    }

}
