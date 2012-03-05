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
final class IdleKick implements Runnable, Listener {

    public int warnIdle = -1;
    public String warnPrivate = null;
    public String warnBroadcast = null;
    public String backBroadcast = null;
    public int kickIdle = -1;
    public String kickReason = null;

    /**
     * Duration (ticks) between checks for idle players.
     */
    private long frequency = 10 * 20; // Default of 10 seconds

    private final Plugin plugin;
    private final EventTracker tracker;
    private int taskId = -1;
    private final Map<Player, Long> warnings = new HashMap<Player, Long>();

    IdleKick(final Plugin plugin, final long frequency, final List<Class<? extends EventListener>> listeners) {
        this.plugin = plugin;
        this.tracker = new EventTracker(plugin, listeners);
        this.frequency = frequency;
    }

    public boolean start() {
        if (this.taskId != -1 || this.frequency <= 0 || (this.warnIdle <= 0 && this.kickIdle <= 0)) return false;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.taskId = this.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(this.plugin, this, this.frequency, this.frequency);
        return (this.taskId != -1);
    }

    public boolean stop() {
        if (this.taskId == -1) return false;

        this.plugin.getServer().getScheduler().cancelTask(this.taskId);
        this.taskId = -1;
        return true;
    }

    @Override
    public void run() {
        final long now = System.currentTimeMillis();
        for (final PlayerEvent event : this.tracker.getLastAll().values()) {
            final Long warned = this.warnings.get(event.getPlayer());
            final long idle = (now - event.getOccurred()) / 1000;

            // Player no longer idle
            if (warned != null && event.getOccurred() > warned) {
                this.warnings.remove(event.getPlayer());
                if (this.backBroadcast != null)
                    Main.messageManager.broadcast(String.format(this.backBroadcast, idle, event.getPlayer().getDisplayName()), MessageLevel.EVENT);

                continue;
            }

            // Warn player if idle too long
            if (this.warnIdle > 0 && warned == null && idle >= this.warnIdle) {
                if (this.warnBroadcast != null) {
                    final String messageBroadcast = String.format(this.warnBroadcast, idle, this.kickIdle, event.getPlayer().getDisplayName());
                    Main.messageManager.broadcast(messageBroadcast, MessageLevel.EVENT);
                }

                if (this.warnPrivate != null) {
                    final String messagePrivate = String.format(this.warnPrivate, idle, this.kickIdle);
                    Main.messageManager.send(event.getPlayer(), messagePrivate, MessageLevel.WARNING);
                }

                this.warnings.put(event.getPlayer(), System.currentTimeMillis());
                continue;
            }

            // Kick player if idle too long
            if (this.kickIdle > 0 && idle >= this.kickIdle) {
                final String message = (this.kickReason != null ? String.format(this.kickReason, this.kickIdle) : null);
                event.getPlayer().kickPlayer(message);
            }
        }

    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.warnings.remove(event.getPlayer());
    }

}
