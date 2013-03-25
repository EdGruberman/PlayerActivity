package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.playeractivity.consumers.away.AwayBack;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;

public final class Toggle implements CommandExecutor {

    private final ConfigurationCourier courier;
    private final AwayBack awayBack;
    private final Away away;
    private final Back back;

    public Toggle(final ConfigurationCourier courier, final AwayBack awayBack, final Away away, final Back back) {
        this.courier = courier;
        this.awayBack = awayBack;
        this.away = away;
        this.back = back;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            this.courier.send(sender, "requires-player", label);
            return true;
        }

        if (this.awayBack.isAway(sender.getName())) {
            this.back.onCommand(sender, command, label, args);
        } else {
            this.away.onCommand(sender, command, label, args);
        }

        return true;
    }

}
