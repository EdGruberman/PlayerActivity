package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import edgruberman.bukkit.playeractivity.consumers.away.AwayBack;
import edgruberman.bukkit.playeractivity.messaging.Courier.ConfigurationCourier;

public final class Toggle implements CommandExecutor {

    private final ConfigurationCourier courier;
    private final AwayBack awayBack;
    private final PluginCommand away;
    private final PluginCommand back;

    public Toggle(final ConfigurationCourier courier, final AwayBack awayBack, final PluginCommand away, final PluginCommand back) {
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
            this.back.execute(sender, label, args);
        } else {
            this.away.execute(sender, label, args);
        }

        return true;
    }

}
