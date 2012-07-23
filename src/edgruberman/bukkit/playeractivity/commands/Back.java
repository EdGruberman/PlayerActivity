package edgruberman.bukkit.playeractivity.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Message;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;

public final class Back extends Executor {

    @Override
    protected boolean execute(final CommandSender sender, final Command command, final String label, final List<String> args) {
        if (!(sender instanceof Player)) {
            Message.manager.tell(sender, "You must be a player in order to use this command", MessageLevel.SEVERE, false);
            return true;
        }

        final Player player = (Player) sender;
        final AwayState state = Main.awayBack.getAwayState(player);
        if (state == null) {
            Message.manager.tell(sender, "You are not currently away", MessageLevel.SEVERE, false);
            return true;
        }

        Main.awayBack.setBack(player);
        Message.manager.broadcast(String.format(Main.awayBack.backFormat, player.getDisplayName(), state.reason, Main.duration(System.currentTimeMillis() - state.since)), MessageLevel.EVENT);
        return true;
    }

}
