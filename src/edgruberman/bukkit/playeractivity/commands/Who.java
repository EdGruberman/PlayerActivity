package edgruberman.bukkit.playeractivity.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;
import edgruberman.bukkit.playeractivity.consumers.IdleNotify;
import edgruberman.bukkit.playeractivity.consumers.ListTag;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;

public final class Who implements CommandExecutor, Listener {

    private final ConfigurationCourier courier;
    private final AwayBack awayBack;
    private final IdleNotify idleNotify;
    private final ListTag listTag;
    private final Map<String, Long> joined = new HashMap<String, Long>();

    public Who(final Plugin plugin, final ConfigurationCourier courier, final AwayBack awayBack, final IdleNotify idleNotify, final ListTag listTag) {
        this.courier = courier;
        this.awayBack = awayBack;
        this.idleNotify = idleNotify;
        this.listTag = listTag;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0) {
            Bukkit.dispatchCommand(sender, "playeractivity:players");
            return true;
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.isOnline() && target.getLastPlayed() == 0) {
            this.courier.send(sender, "player-not-found", args[0]);
            return true;
        }

        // disconnected
        if (!target.isOnline() || !target.getPlayer().hasPermission("playeractivity.track.who")) {
            final String duration = Main.readableDuration(System.currentTimeMillis() - target.getLastPlayed());
            this.courier.send(sender, "who.disconnected", target.getName(), duration);
            return true;
        }

        final long now = System.currentTimeMillis();
        final String connected =  (this.joined.containsKey(target.getPlayer().getName()) ? Main.readableDuration(now - this.joined.get(target.getPlayer().getName())) : this.courier.format("who.+unknown-connected"));

        // away
        if (this.awayBack != null && this.awayBack.isAway(target.getPlayer())) {
            final AwayState state = this.awayBack.getAwayState(target.getPlayer().getName());
            this.courier.send(sender, "who.connected-away", target.getPlayer().getDisplayName(), connected, Main.readableDuration(now - state.since), state.reason);
            return true;
        }

        // idle
        if (this.idleNotify != null && this.listTag.tracker.getIdle().contains(target.getPlayer())) {
            this.courier.send(sender, "who.connected-idle", target.getPlayer().getDisplayName(), connected, Main.readableDuration(now - this.idleNotify.tracker.getLastFor(target.getPlayer())));
            return true;
        }

        // connected
        this.courier.send(sender, "who.connected", target.getPlayer().getDisplayName(), connected);
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.joined.put(event.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.joined.remove(event.getPlayer().getName());
    }

}
