package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Message;
import edgruberman.bukkit.playeractivity.commands.util.Action;
import edgruberman.bukkit.playeractivity.commands.util.Context;
import edgruberman.bukkit.playeractivity.commands.util.Parser;
import edgruberman.bukkit.playeractivity.consumers.AwayState;

public final class Away extends Action {

    public Away(final JavaPlugin plugin) {
        super(plugin, "away");
    }

    @Override
    public boolean perform(final Context context) {
        if (!(context.sender instanceof Player)) {
            Message.manager.send(context.sender, "You must be a player in order to use this command", MessageLevel.SEVERE);
            return true;
        }

        final Player player = (Player) context.sender;
        final AwayState state = Main.awayBack.getAwayState(player);
        if (state != null) {
            Message.manager.send(context.sender, "You have been away " + Main.duration(System.currentTimeMillis() - state.since) + (state.reason != null ? " for " + state.reason : ""), MessageLevel.SEVERE);
            return true;
        }

        String reason = Main.awayBack.defaultReason;
        if (context.arguments.size() >= 1) reason = Parser.join(context.arguments).trim();

        Main.awayBack.setAway(player, reason);
        Message.manager.broadcast(String.format(Main.awayBack.awayFormat, player.getDisplayName(), reason), MessageLevel.EVENT);
        return true;
    }

}
