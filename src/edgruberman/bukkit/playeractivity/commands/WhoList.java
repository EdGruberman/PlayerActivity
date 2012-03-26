package edgruberman.bukkit.playeractivity.commands;

import java.util.ArrayList;
import java.util.List;

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
        final List<String> list = new ArrayList<String>();
        for (final Player player : context.sender.getServer().getOnlinePlayers())
            list.add(this.tag(player));

        Message.manager.tell(context.sender, String.format(WhoList.format, Parser.join(list, WhoList.delimiter)), MessageLevel.CONFIG, false);
        return true;
    }

    private String tag(final Player player) {
        final String name = String.format(WhoList.name, player.getDisplayName());

        if (Main.awayBack != null && Main.awayBack.isAway(player))
            return String.format(WhoList.away, name);

        if (Main.idleNotify != null && Main.idleNotify.tracker.idlePublisher.getIdle().contains(player))
            return String.format(WhoList.idle, name);

        return name;
    }

}
