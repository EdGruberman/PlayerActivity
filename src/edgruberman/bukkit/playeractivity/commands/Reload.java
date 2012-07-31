package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.Messenger;

public final class Reload implements CommandExecutor {

    private final Plugin plugin;
    private final Messenger messenger;

    public Reload(final Plugin plugin, final Messenger messenger) {
        this.plugin = plugin;
        this.messenger = messenger;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        this.plugin.onDisable();
        this.plugin.onEnable();
        this.messenger.tell(sender, "reload");
        return true;
    }

}
