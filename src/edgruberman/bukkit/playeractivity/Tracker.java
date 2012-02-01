package edgruberman.bukkit.playeractivity;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;

public final class Tracker implements Runnable, Listener {

    public Integer warnIdle = null;
    public String warnPrivate = null;
    public String warnBroadcast = null;
    public String backBroadcast = null;
    public Integer kickIdle = null;
    public String kickReason = null;

    /**
     * Duration (ticks) between checks for idle players.
     */
    private long frequency = 20;

    public final Plugin plugin;
    private final Map<Player, Status> statuses = new HashMap<Player, Status>();

    Tracker(final Plugin plugin, long frequency, final List<Class<? extends EventListener>> listeners) {
        this.plugin = plugin;
        this.frequency = frequency;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

        for (Class<? extends EventListener> listener : listeners)
            try {
                listener.getConstructor(Tracker.class).newInstance(this);
            } catch (IllegalArgumentException e) {
                Main.messageManager.log("Error instantiating EventListener " + listener.getName(), MessageLevel.SEVERE, e);
            } catch (SecurityException e) {
                Main.messageManager.log("Error instantiating EventListener " + listener.getName(), MessageLevel.SEVERE, e);
            } catch (InstantiationException e) {
                Main.messageManager.log("Error instantiating EventListener " + listener.getName(), MessageLevel.SEVERE, e);
            } catch (IllegalAccessException e) {
                Main.messageManager.log("Error instantiating EventListener " + listener.getName(), MessageLevel.SEVERE, e);
            } catch (InvocationTargetException e) {
                Main.messageManager.log("Error instantiating EventListener " + listener.getName(), MessageLevel.SEVERE, e);
            } catch (NoSuchMethodException e) {
                Main.messageManager.log("Error instantiating EventListener " + listener.getName(), MessageLevel.SEVERE, e);
            }

        if (this.frequency > 0)
            this.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(this.plugin, this, 0, this.frequency);
    }

    public Status getStatus(final Player player) {
        return this.statuses.get(player);
    }

    /**
     * Record last activity for player.
     * (This could be called on high frequency events such as PLAYER_MOVE.)
     *
     * @param player player to record this as last activity for
     * @param occured milliSeconds from midnight, January 1, 1970 UTC the activity was performed at
     * @param type event type that player engaged in
     */
    public void recordActivity(final Player player, final long occured, final Event event) {
        if (!this.statuses.containsKey(player)) this.statuses.put(player, new Status(player));
        Status status = this.statuses.get(player);
        status.setLastActivity(occured);
        if (status.isWarned) {
            status.isWarned = false;
            if (this.backBroadcast != null)
                Main.messageManager.broadcast(String.format(this.backBroadcast, player.getDisplayName()), MessageLevel.EVENT);
        }
    }

    @Override
    public void run() {
        Long idleFor;
        for (Status status : this.statuses.values()) {
            idleFor = status.idleFor();
            if (idleFor == null) continue;
            idleFor /= 1000;

            if (this.warnIdle != null && idleFor >= this.warnIdle && !status.isWarned) {
                String messageBroadcast = String.format(this.warnBroadcast, status.getPlayer().getDisplayName(), idleFor, this.kickIdle);
                Main.messageManager.broadcast(messageBroadcast, MessageLevel.EVENT);
                String messagePrivate = String.format(this.warnPrivate, idleFor, this.kickIdle);
                Main.messageManager.send(status.getPlayer(), messagePrivate, MessageLevel.WARNING);
                status.isWarned = true;
            }

            if (this.kickIdle != null && idleFor >= this.kickIdle) {
                String message = String.format(this.kickReason, this.kickIdle);
                status.getPlayer().kickPlayer(message);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.statuses.remove(event.getPlayer());
    }

}