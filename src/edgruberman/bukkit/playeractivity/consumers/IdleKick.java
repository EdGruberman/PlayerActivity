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
    private final String track;

    public IdleKick(final Plugin plugin, final long idle, final List<String> activity, final ConfigurationCourier courier, final String track) {
        this.courier = courier;
        this.track = track;

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
        if (!idle.player.hasPermission(this.track)) return;
        idle.player.kickPlayer(this.courier.format("+idle-kick-reason", Main.readableDuration(this.tracker.getIdleThreshold())));
    }

}
