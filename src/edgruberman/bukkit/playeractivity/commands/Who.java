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
import edgruberman.bukkit.playeractivity.consumers.listtag.ListTag;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;

public final class Who implements CommandExecutor, Listener {

    private final ConfigurationCourier courier;
    private final ListTag listTag;
    private final Map<String, Long> joined = new HashMap<String, Long>();

    public Who(final Plugin plugin, final ConfigurationCourier courier, final ListTag listTag) {
        this.courier = courier;
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

        // connected
        final long now = System.currentTimeMillis();
        final String connected = ( this.joined.containsKey(target.getPlayer().getName()) ? Main.readableDuration(now - this.joined.get(target.getPlayer().getName())) : this.courier.format("who.+unknown-connected") );
        this.courier.send(sender, "who.connected", target.getPlayer().getDisplayName(), connected
                , ( this.listTag != null ? this.listTag.getTagDescription(target.getPlayer()) : null )
                , ( this.listTag != null && this.listTag.getAttached(target.getPlayer()).size() > 0 ? 1 : 0 ));
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
