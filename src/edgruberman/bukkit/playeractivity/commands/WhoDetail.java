package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.commands.util.Action;
import edgruberman.bukkit.playeractivity.commands.util.Context;
import edgruberman.bukkit.playeractivity.commands.util.Handler;
import edgruberman.bukkit.playeractivity.commands.util.Parser;
import edgruberman.bukkit.playeractivity.consumers.AwayState;

public final class WhoDetail extends Action {

    public static String connected = null;
    public static String away = null;
    public static String idle = null;
    public static String disconnected = null;

    WhoDetail(final Handler handler) {
        super(handler, "detail");
    }

    @Override
    public boolean matches(final Context context) {
        if (super.matches(context)) return true;

        if (context.arguments.size() == 0) return false;

        if (context.arguments.get(0).equalsIgnoreCase("list")) return false;

        return true;
    }

    @Override
    public boolean perform(final Context context) {
        final OfflinePlayer target = Parser.parsePlayer(context, (context.arguments.size() == 1 ? 0 : 1));
        if (target == null) {
            Main.messageManager.tell(context.sender, "Unable to determine player", MessageLevel.WARNING, false);
            return false;
        }

        final String duration = Main.duration(System.currentTimeMillis() - target.getLastPlayed());
        if (!target.isOnline()) {
            Main.messageManager.tell(context.sender, String.format(WhoDetail.disconnected, target.getName(), duration), MessageLevel.CONFIG, false);
            return true;
        }

        Main.messageManager.tell(context.sender, this.connected(target.getPlayer(), duration), MessageLevel.CONFIG, false);
        return true;
    }

    private String connected(final Player player, final String durationLast) {
        final String connected = String.format(WhoDetail.connected, player.getDisplayName(), durationLast);

        if (Main.awayBack != null && Main.awayBack.isAway(player)) {
            final AwayState state = Main.awayBack.getAwayState(player);
            return String.format(WhoDetail.away, connected, Main.duration(System.currentTimeMillis() - state.since), state.reason);
        }

        if (Main.idleKick != null && Main.idleKick.warn.idlePublisher.getIdle().contains(player)) {
            return String.format(WhoDetail.idle, connected, Main.duration(System.currentTimeMillis() - Main.idleKick.warn.getLastFor(player)));
        }

        return this.name;
    }

}
