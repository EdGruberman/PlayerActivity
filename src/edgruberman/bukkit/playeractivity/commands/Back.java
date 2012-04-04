package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Message;
import edgruberman.bukkit.playeractivity.commands.util.Action;
import edgruberman.bukkit.playeractivity.commands.util.Context;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;

public final class Back extends Action {

    public Back(final JavaPlugin plugin, final String name) {
        super(plugin, name, "back");
    }

    @Override
    public boolean perform(final Context context) {
        if (!(context.sender instanceof Player)) {
            Message.manager.tell(context.sender, "You must be a player in order to use this command", MessageLevel.SEVERE, false);
            return true;
        }

        final Player player = (Player) context.sender;
        final AwayState state = Main.awayBack.getAwayState(player);
        if (state == null) {
            Message.manager.tell(context.sender, "You are not currently away", MessageLevel.SEVERE, false);
            return true;
        }

        Main.awayBack.setBack(player);
        Message.manager.broadcast(String.format(Main.awayBack.backFormat, player.getDisplayName(), state.reason, Main.duration(System.currentTimeMillis() - state.since)), MessageLevel.EVENT);
        return true;
    }

}
