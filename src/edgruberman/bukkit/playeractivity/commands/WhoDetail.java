package edgruberman.bukkit.playeractivity.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Message;
import edgruberman.bukkit.playeractivity.commands.util.Action;
import edgruberman.bukkit.playeractivity.commands.util.Context;
import edgruberman.bukkit.playeractivity.commands.util.Handler;
import edgruberman.bukkit.playeractivity.commands.util.Parser;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;

public final class WhoDetail extends Action implements Listener {

    public static String connected = null;
    public static String away = null;
    public static String idle = null;
    public static String disconnected = null;

    WhoDetail(final Handler handler) {
        super(handler, "detail");
        handler.command.getPlugin().getServer().getPluginManager().registerEvents(this, handler.command.getPlugin());
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
            Message.manager.tell(context.sender, "Unable to determine player", MessageLevel.WARNING, false);
            return false;
        }

        if (!target.isOnline() || target.getPlayer().hasPermission("playeractivity.who.hide.detail")) {
            final String duration = Main.duration(System.currentTimeMillis() - target.getLastPlayed());
            Message.manager.tell(context.sender, String.format(WhoDetail.disconnected, target.getName(), duration), MessageLevel.CONFIG, false);
            return true;
        }

        Message.manager.tell(context.sender, this.connected(target.getPlayer()), MessageLevel.CONFIG, false);
        return true;
    }

    private String connected(final Player player) {
        final long now = System.currentTimeMillis();
        final String connected = String.format(WhoDetail.connected, player.getDisplayName(), Main.duration(now - this.joined.get(player)));

        if (Main.awayBack != null && Main.awayBack.isAway(player)) {
            final AwayState state = Main.awayBack.getAwayState(player);
            return String.format(WhoDetail.away, connected, Main.duration(now - state.since), state.reason);
        }

        if (Main.idleNotify != null && Main.listTag.tracker.idlePublisher.getIdle().contains(player)) {
            return String.format(WhoDetail.idle, connected, Main.duration(now - Main.idleNotify.tracker.getLastFor(player)));
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

}
