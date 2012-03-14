package edgruberman.bukkit.playeractivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;

/**
 * Consumer of the PlayerActivity plugin that will warn and/or kick players for being idle.
 */
public final class IdleKick implements Runnable, Listener {

    public int warnIdle = -1;
    public String warnPrivate = null;
    public String warnBroadcast = null;
    public String backBroadcast = null;
    public int kickIdle = -1;
    public String kickReason = null;

    /**
     * Duration (ticks) between checks for idle players.
     */
    public long frequency = 10 * 20; // Default of 10 seconds

    public final Plugin plugin;
    public final EventTracker tracker;
    private int taskId = -1;
    private final Map<Player, Long> idles = new HashMap<Player, Long>();
    private final String permissionIgnore;

    IdleKick(final Plugin plugin){
        this.plugin = plugin;
        this.tracker = new EventTracker(plugin);
        this.permissionIgnore = plugin.getDescription().getName().toLowerCase() + ".idlekick.ignore";
    }

    IdleKick(final Plugin plugin, final long frequency, final List<Interpreter> interpreters) {
        this.plugin = plugin;
        this.tracker = new EventTracker(plugin, interpreters);
        this.frequency = frequency;
        this.permissionIgnore = plugin.getDescription().getName().toLowerCase() + ".idlekick.ignore";
    }

    public boolean start() {
        if (
                   this.taskId != -1
                || this.frequency <= 0
                || (this.warnIdle <= 0 && this.kickIdle <= 0)
                || this.tracker.getInterpreters().size() == 0
        )
            return false;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.taskId = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, this.frequency, this.frequency);
        return (this.taskId != -1);
    }

    public boolean stop() {
        if (this.taskId == -1) return false;

        this.plugin.getServer().getScheduler().cancelTask(this.taskId);
        this.taskId = -1;
        return true;
    }

    public void reset() {
        this.stop();
        this.tracker.clear();
    }

    @Override
    public void run() {
        final long now = System.currentTimeMillis();
        for (final Map.Entry<Player, Long> event : this.tracker.getLastAll().entrySet()) {
            if (event.getKey().hasPermission(this.permissionIgnore)) continue;

            final Long idle = this.idles.get(event.getKey());
            final long last = (now - event.getValue()) / 1000;

            // Player no longer idle
            if (idle != null && event.getValue() != idle) {
                this.idles.remove(event.getKey());
                if (this.backBroadcast != null)
                    Main.messageManager.broadcast(String.format(this.backBroadcast, (now - idle) / 1000, event.getKey().getDisplayName()), MessageLevel.EVENT);

                continue;
            }

            // Warn player if idle too long
            if (this.warnIdle > 0 && idle == null && last >= this.warnIdle) {
                if (this.warnBroadcast != null) {
                    final String messageBroadcast = String.format(this.warnBroadcast, last, this.kickIdle, event.getKey().getDisplayName());
                    Main.messageManager.broadcast(messageBroadcast, MessageLevel.EVENT);
                }

                if (this.warnPrivate != null) {
                    final String messagePrivate = String.format(this.warnPrivate, last, this.kickIdle);
                    Main.messageManager.send(event.getKey(), messagePrivate, MessageLevel.WARNING);
                }

                this.idles.put(event.getKey(), event.getValue());
                continue;
            }

            // Kick player if idle too long
            if (this.kickIdle > 0 && last >= this.kickIdle) {
                final String message = (this.kickReason != null ? String.format(this.kickReason, this.kickIdle) : null);
                event.getKey().kickPlayer(message);
            }
        }

    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.idles.remove(event.getPlayer());
    }

}
