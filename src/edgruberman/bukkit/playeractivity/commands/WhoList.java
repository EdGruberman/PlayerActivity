package edgruberman.bukkit.playeractivity.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Message;
import edgruberman.bukkit.playeractivity.commands.util.Action;
import edgruberman.bukkit.playeractivity.commands.util.Context;
import edgruberman.bukkit.playeractivity.commands.util.Handler;
import edgruberman.bukkit.playeractivity.commands.util.Parser;

public final class WhoList extends Action {

    public static String format = null;
    public static String delimiter = null;
    public static String name = null;
    public static String away = null;
    public static String idle = null;

    WhoList(final Handler handler) {
        super(handler, "list");
    }

    @Override
    public boolean perform(final Context context) {
        final List<Player> sorted = Arrays.asList(context.sender.getServer().getOnlinePlayers());
        Collections.sort(sorted, new Comparator<Player>() {
            @Override
            public int compare(final Player p1, final Player p2) {
                return ChatColor.stripColor(p1.getDisplayName()).compareTo(ChatColor.stripColor(p2.getDisplayName()));
            }
        });

        final List<String> list = new ArrayList<String>();
        for (final Player player : sorted)
            if (!player.hasPermission("playeractivity.who.hide.list"))
                list.add(this.tag(player));

        Message.manager.tell(context.sender, String.format(WhoList.format, Parser.join(list, WhoList.delimiter), list.size()), MessageLevel.CONFIG, false);
        return true;
    }

    private String tag(final Player player) {
        final String name = String.format(WhoList.name, player.getDisplayName());

        if (Main.awayBack != null && Main.awayBack.isAway(player))
            return String.format(WhoList.away, name);

        if (Main.listTag != null && Main.listTag.tracker.idlePublisher.getIdle().contains(player))
            return String.format(WhoList.idle, name);

        return name;
    }

}
