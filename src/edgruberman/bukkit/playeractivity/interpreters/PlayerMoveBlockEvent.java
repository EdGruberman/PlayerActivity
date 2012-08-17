package edgruberman.bukkit.playeractivity.interpreters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.StatusTracker;

/** SCIMP - Self Contained Inner class Manager Pattern */
public class PlayerMoveBlockEvent extends Interpreter {

    @Override
    public void register(final StatusTracker tracker) {
        super.register(tracker);
        MovementTracker.register(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final edgruberman.bukkit.playeractivity.PlayerMoveBlockEvent event) {
        this.record(event.getPlayer(), event);
    }

    @Override
    public void clear() {
        super.clear();
        MovementTracker.deregister(this);
    }



    private static class MovementTracker implements Listener {

        private static MovementTracker instance = null;
        private static List<Interpreter> registered = new ArrayList<Interpreter>();

        private static void register(final PlayerMoveBlockEvent interpreter) {
            if (MovementTracker.instance == null)
                MovementTracker.instance = new MovementTracker(interpreter.tracker.getPlugin());

            MovementTracker.registered.add(interpreter);
        }

        private static void deregister(final PlayerMoveBlockEvent interpreter) {
            MovementTracker.registered.remove(interpreter);
            if (MovementTracker.registered.size() != 0) return;

            MovementTracker.instance.clear();
            MovementTracker.instance = null;
        }



        private final Map<Player, Location> lastBlockChange = new HashMap<Player, Location>();

        private MovementTracker(final Plugin plugin) {
            for (final Player player : Bukkit.getOnlinePlayers()) this.lastBlockChange.put(player, player.getLocation());
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        private void clear() {
            HandlerList.unregisterAll(this);
            this.lastBlockChange.clear();
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onEvent(final org.bukkit.event.player.PlayerMoveEvent event) {
            final Location last = this.lastBlockChange.get(event.getPlayer());
            final Location to = event.getTo();
            if ((last.getBlockX() == to.getBlockX()) && (last.getBlockZ() == to.getBlockZ()) && (last.getBlockY() == to.getBlockY())) return;

            this.lastBlockChange.put(event.getPlayer(), to);
            Bukkit.getServer().getPluginManager()
                    .callEvent(new edgruberman.bukkit.playeractivity.PlayerMoveBlockEvent(event.getPlayer(), last, to));
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(final PlayerJoinEvent event) {
            this.lastBlockChange.put(event.getPlayer(), event.getPlayer().getLocation());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(final PlayerQuitEvent event) {
            this.lastBlockChange.remove(event.getPlayer());
        }

    }

}
