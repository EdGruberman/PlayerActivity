package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.playeractivity.commands.util.Handler;

public final class Who extends Handler {

    public Who(final JavaPlugin plugin, final String name) {
        super(plugin, name, "who");
        new WhoList(this).setDefault();
        new WhoDetail(this);
    }

}
