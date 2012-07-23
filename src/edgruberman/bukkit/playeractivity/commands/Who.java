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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Message;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;

public final class Who extends Executor implements Listener {

    public static String format = null;
    public static String delimiter = null;
    public static String name = null;
    public static String away = null;
    public static String idle = null;

    public static String connected = null;
    public static String detailAway = null;
    public static String detailIdle = null;
    public static String disconnected = null;

    public Who(final Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected boolean execute(final CommandSender sender, final Command command, final String label, final List<String> args) {
        // usage: /who
        if (args.size() == 0) {
            final List<Player> sorted = Arrays.asList(sender.getServer().getOnlinePlayers());
            Collections.sort(sorted, new ColorStrippedStringComparator());

            final List<String> list = new ArrayList<String>();
            for (final Player player : sorted)
                if (!player.hasPermission("playeractivity.who.hide"))
                    list.add(this.tag(player));

            Message.manager.tell(sender, String.format(Who.format, Who.join(list, Who.delimiter), list.size()), MessageLevel.CONFIG, false);
            return true;
        }

        // usage: /who <Player>
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args.get(0));
        if (target == null) {
            Message.manager.tell(sender, "Unable to determine player", MessageLevel.WARNING, false);
            return false;
        }

        if (!target.isOnline() || target.getPlayer().hasPermission("playeractivity.who.hide")) {
            final String duration = Main.duration(System.currentTimeMillis() - target.getLastPlayed());
            Message.manager.tell(sender, String.format(Who.disconnected, target.getName(), duration), MessageLevel.CONFIG, false);
            return true;
        }

        Message.manager.tell(sender, this.connected(target.getPlayer()), MessageLevel.CONFIG, false);
        return true;
    }

    private String tag(final Player player) {
        final String name = String.format(Who.name, player.getDisplayName());

        if (Main.awayBack != null && Main.awayBack.isAway(player))
            return String.format(Who.away, name);

        if (Main.listTag != null && Main.listTag.tracker.idlePublisher.getIdle().contains(player))
            return String.format(Who.idle, name);

        return name;
    }

    private String connected(final Player player) {
        final long now = System.currentTimeMillis();
        final String connected = String.format(Who.connected, player.getDisplayName(), Main.duration(now - this.joined.get(player)));

        if (Main.awayBack != null && Main.awayBack.isAway(player)) {
            final AwayState state = Main.awayBack.getAwayState(player);
            return String.format(Who.detailAway, connected, Main.duration(now - state.since), state.reason);
        }

        if (Main.idleNotify != null && Main.listTag.tracker.idlePublisher.getIdle().contains(player)) {
            return String.format(Who.detailIdle, connected, Main.duration(now - Main.idleNotify.tracker.getLastFor(player)));
        }

        return connected;
    }

    private final Map<Player, Long> joined = new HashMap<Player, Long>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.joined.put(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.joined.remove(event.getPlayer());
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

    private final class ColorStrippedStringComparator implements Comparator<Player> {

        @Override
        public int compare(final Player p1, final Player p2) {
            return ChatColor.stripColor(p1.getDisplayName()).compareTo(ChatColor.stripColor(p2.getDisplayName()));
        }

    }

}
