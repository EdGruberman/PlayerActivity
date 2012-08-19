package edgruberman.bukkit.playeractivity;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.util.DynamicEventGenerator;

/** called whenever a player moves at least a block of total distance along any of the axes */
public class PlayerMoveBlockEvent extends PlayerEvent {

    private static final HandlerList handlers = new MovementTracker();

    private final Location from;
    private final Location to;

    public PlayerMoveBlockEvent(final Player who, final Location from, final Location to) {
        super(who);
        this.from = from;
        this.to = to;
    }

    /** location that has a different block than to */
    public Location getFrom() {
        return this.from;
    }

    /** location that has a different block than from */
    public Location getTo() {
        return this.to;
    }

    @Override
    public HandlerList getHandlers() {
        return PlayerMoveBlockEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return PlayerMoveBlockEvent.handlers;
    }



    public static class MovementTracker extends DynamicEventGenerator implements Listener {

        private static Plugin plugin = null;

        public static void initialize(final Plugin plugin) {
            if (MovementTracker.plugin != null)
                ((MovementTracker) PlayerMoveBlockEvent.handlers).stop();

            MovementTracker.plugin = plugin;
            if (PlayerMoveBlockEvent.handlers.getRegisteredListeners().length >= 1)
                ((MovementTracker) PlayerMoveBlockEvent.handlers).start();
        }

        private final Map<Player, Location> last = new HashMap<Player, Location>();

        @Override
        protected void start() {
            if (MovementTracker.plugin == null) return;

            for (final Player player : Bukkit.getOnlinePlayers()) this.last.put(player, player.getLocation());
            Bukkit.getPluginManager().registerEvents(this, MovementTracker.plugin);
        }

        @Override
        protected void stop() {
            HandlerList.unregisterAll(this);
            this.last.clear();
        }

        @EventHandler
        public void onPluginDisable(final PluginDisableEvent disabled) {
            if (disabled.getPlugin() != MovementTracker.plugin) return;

            this.stop();
            MovementTracker.plugin = null;
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onEvent(final org.bukkit.event.player.PlayerMoveEvent event) {
            final Location last = this.last.get(event.getPlayer());
            final Location to = event.getTo();
            if (Math.abs(last.getX() - to.getX()) < 1 && Math.abs(last.getZ() - to.getZ()) < 1 && Math.abs(last.getY() - to.getY()) < 1) return;

            this.last.put(event.getPlayer(), to);
            Bukkit.getServer().getPluginManager().callEvent(new PlayerMoveBlockEvent(event.getPlayer(), last, to));
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(final PlayerJoinEvent event) {
            this.last.put(event.getPlayer(), event.getPlayer().getLocation());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(final PlayerQuitEvent event) {
            this.last.remove(event.getPlayer());
        }

    }

}
