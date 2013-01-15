package edgruberman.bukkit.playeractivity.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        // ---- usage: /who

        if (args.length == 0) {
            final List<Player> sorted = Arrays.asList(sender.getServer().getOnlinePlayers());
            Collections.sort(sorted, new ColorStrippedDisplayNameComparator());

            final List<String> list = new ArrayList<String>();
            for (final Player player : sorted)
                if (!player.hasPermission("playeractivity.who.hide"))
                    list.add(this.tag(player));

            this.courier.send(sender, "who.list.format", Who.join(list, this.courier.format("who.list.+delimiter")), list.size());
            return true;
        }

        // ---- usage: /who <Player>

        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            this.courier.send(sender, "player-not-found", args[0]);
            return true;
        }

        // Disconnected
        if (!target.isOnline() || target.getPlayer().hasPermission("playeractivity.who.hide")) {
            final String duration = Main.readableDuration(System.currentTimeMillis() - target.getLastPlayed());
            this.courier.send(sender, "who.disconnected", target.getName(), duration);
            return true;
        }

        final long now = System.currentTimeMillis();
        final String connected =  (this.joined.containsKey(target.getPlayer().getName()) ? Main.readableDuration(now - this.joined.get(target.getPlayer().getName())) : this.courier.format("who.+unknown-connected"));

        // Away
        if (this.awayBack != null && this.awayBack.isAway(target.getPlayer())) {
            final AwayState state = this.awayBack.getAwayState(target.getPlayer().getName());
            this.courier.send(sender, "who.connected-away", target.getPlayer().getDisplayName(), connected, Main.readableDuration(now - state.since), state.reason);
            return true;
        }

        // Idle
        if (this.idleNotify != null && this.listTag.tracker.getIdle().contains(target.getPlayer())) {
            this.courier.send(sender, "who.connected-idle", target.getPlayer().getDisplayName(), connected, Main.readableDuration(now - this.idleNotify.tracker.getLastFor(target.getPlayer())));
            return true;
        }

        // Connected
        this.courier.send(sender, "who.connected", target.getPlayer().getDisplayName(), connected);
        return true;
    }

    // # 0 = Name, 1 = Display Name, 2 = List Name, 3 = Equivalence (1:Name=Display,2:Name=List,4:Display=List)
    private String tag(final Player player) {
        int equivalence = ( player.getName().equals(player.getDisplayName()) ? 1 : 0 );
        equivalence += ( player.getName().equals(player.getPlayerListName()) ? 2 : 0 );
        equivalence += ( player.getDisplayName().equals(player.getPlayerListName()) ? 4 : 0 );
        final String name = this.courier.format("who.list.+player", player.getName(), player.getDisplayName(), player.getPlayerListName(), equivalence);

        if (this.awayBack != null && this.awayBack.isAway(player))
            return this.courier.format("who.list.+tag-away", name);

        if (this.listTag != null && this.listTag.tracker.getIdle().contains(player.getName()))
            return this.courier.format("who.list.+tag-idle", name);

        return name;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.joined.put(event.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.joined.remove(event.getPlayer().getName());
    }

    /**
     * Concatenate a collection with a delimiter.
     *
     * @param col entries to concatenate
     * @param delim placed between each entry
     * @return entries concatenated; empty string if no entries
     */
    private static String join(final Collection<? extends String> col, final String delim) {
        if (col == null || col.isEmpty()) return "";

        final StringBuilder sb = new StringBuilder();
        for (final String s : col) sb.append(s + delim);
        sb.delete(sb.length() - delim.length(), sb.length());

        return sb.toString();
    }

    private final class ColorStrippedDisplayNameComparator implements Comparator<Player> {

        @Override
        public int compare(final Player p1, final Player p2) {
            return ChatColor.stripColor(p1.getDisplayName()).compareTo(ChatColor.stripColor(p2.getDisplayName()));
        }

    }

}
