package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;

public final class Back implements CommandExecutor {

    private final ConfigurationCourier courier;
    private final AwayBack awayBack;

    public Back(final ConfigurationCourier courier, final AwayBack awayBack) {
        this.courier = courier;
        this.awayBack = awayBack;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            this.courier.send(sender, "requires-player", label);
            return true;
        }

        final Player player = (Player) sender;
        final AwayState state = this.awayBack.getAwayState(player.getName());
        if (state == null) {
            this.courier.send(sender, "back-already");
            return true;
        }

        this.awayBack.setBack(player);
        this.courier.broadcast("back", player.getDisplayName(), state.reason, Main.readableDuration(System.currentTimeMillis() - state.since));
        return true;
    }

}
