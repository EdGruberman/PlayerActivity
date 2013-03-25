package edgruberman.bukkit.playeractivity.consumers;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.PlayerIdle;
import edgruberman.bukkit.playeractivity.StatusTracker;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;

/** kick players for being idle */
public final class IdleKick implements Observer, Listener {

    public final StatusTracker tracker;

    private final ConfigurationCourier courier;
    private final String track;

    private String kicked = null;

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

        Bukkit.getPluginManager().registerEvents(this, plugin);

        plugin.getLogger().log(Level.CONFIG, "Idle Kick consumer enabled; threshold: " + Main.readableDuration(this.tracker.getIdleThreshold()));
    }

    public void unload() {
        this.tracker.clear();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final PlayerIdle idle = (PlayerIdle) arg;
        if (!idle.player.hasPermission(this.track)) return;
        this.kicked = idle.player.getName();
        idle.player.kickPlayer(this.courier.format("+idle-kick-reason", Main.readableDuration(this.tracker.getIdleThreshold())));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKick(final PlayerKickEvent kick) {
        if (this.kicked == null) return;
        if (!kick.getPlayer().getName().equals(this.kicked)) {
            this.kicked = null;
            return;
        }

        this.courier.broadcast("idle-kick-leave", kick.getPlayer().getDisplayName(), kick.getReason(), Main.readableDuration(this.tracker.getIdleThreshold()), kick.getLeaveMessage());
        kick.setLeaveMessage(null);
    }

}
