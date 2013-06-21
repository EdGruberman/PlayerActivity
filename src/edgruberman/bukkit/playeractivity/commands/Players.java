package edgruberman.bukkit.playeractivity.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.playeractivity.consumers.listtag.ListTag;
import edgruberman.bukkit.playeractivity.messaging.Courier.ConfigurationCourier;
import edgruberman.bukkit.playeractivity.util.JoinList;

public final class Players implements CommandExecutor {

    private final ConfigurationCourier courier;
    private final ListTag listTag;

    public Players(final ConfigurationCourier courier, final ListTag listTag) {
        this.courier = courier;
        this.listTag = listTag;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final List<Player> sorted = Arrays.asList(sender.getServer().getOnlinePlayers());
        Collections.sort(sorted, new ColorStrippedDisplayNameComparator());

        final JoinList<String> list = new JoinList<String>(this.courier.getSection("players"));
        for (final Player player : sorted)
            if (player.hasPermission("playeractivity.track.players"))
                list.add(this.listTag.getTagDisplayName(player));

        this.courier.send(sender, "players.format", list, list.size());
        return true;
    }



    private static final class ColorStrippedDisplayNameComparator implements Comparator<Player> {

        @Override
        public int compare(final Player p1, final Player p2) {
            return ChatColor.stripColor(p1.getDisplayName()).compareTo(ChatColor.stripColor(p2.getDisplayName()));
        }

    }

}
