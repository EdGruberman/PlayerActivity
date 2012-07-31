package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Messenger;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;

public final class Back implements CommandExecutor {

    private final Messenger messenger;
    private final AwayBack awayBack;

    public Back(final Messenger messenger, final AwayBack awayBack) {
        this.messenger = messenger;
        this.awayBack = awayBack;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            this.messenger.tell(sender, "requiresPlayer");
            return true;
        }

        final Player player = (Player) sender;
        final AwayState state = this.awayBack.getAwayState(player);
        if (state == null) {
            this.messenger.tell(sender, "backNotAway");
            return true;
        }

        this.awayBack.setBack(player);
        this.messenger.broadcast("backBroadcast", player.getDisplayName(), state.reason, Main.readableDuration(System.currentTimeMillis() - state.since));
        return true;
    }

}
