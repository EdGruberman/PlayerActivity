package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.messaging.couriers.ConfigurationCourier;

public final class Reload implements CommandExecutor {

    private final Plugin plugin;
    private final ConfigurationCourier courier;

    public Reload(final Plugin plugin, final ConfigurationCourier courier) {
        this.plugin = plugin;
        this.courier = courier;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        this.plugin.onDisable();
        this.plugin.onEnable();
        this.courier.send(sender, "reload", this.plugin.getName());
        return true;
    }

}
