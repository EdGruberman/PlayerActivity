package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;

public final class Main extends JavaPlugin {

    public static MessageManager messageManager;
    public static IdleKick idleKick = null;

    private ConfigurationFile configurationFile;

    @Override
    public void onLoad() {
        Main.messageManager = new MessageManager(this);
        this.configurationFile = new ConfigurationFile(this);
    }

    @Override
    public void onEnable() {
        this.loadConfiguration();
    }

    @Override
    public void onDisable() {
        if (this.configurationFile.isSaveQueued()) this.configurationFile.save();
        this.getServer().getScheduler().cancelTasks(this);
    }

    public void loadConfiguration() {
        if (Main.idleKick != null) Main.idleKick.stop();
        final FileConfiguration config = this.configurationFile.load();
        this.loadIdleKick(config.getConfigurationSection("IdleKick"));
    }

    private void loadIdleKick(final ConfigurationSection config) {
        if (!config.getBoolean("enabled", false)) return;

        final Long frequency = config.getLong("frequency");
        if (frequency == null || frequency <= 0) return;

        final List<Class<? extends EventFilter>> filters = new ArrayList<Class<? extends EventFilter>>();
        for (final String reference : config.getStringList("activity")) {
            Class<? extends EventFilter> filter = null;
            try {
                filter = Class.forName("edgruberman.bukkit.playeractivity.filters." + reference).asSubclass(EventFilter.class);
            } catch (final ClassNotFoundException e) {
                // Ignore to try below
            }
            try {
                filter = Class.forName(reference).asSubclass(EventFilter.class);
            } catch (final ClassNotFoundException e1) {
                Main.messageManager.log("Unsupported Listener: " + reference, MessageLevel.WARNING);
                continue;
            }
            filters.add(filter);
        }
        if (filters.size() == 0) return;

        if (Main.idleKick == null) Main.idleKick = new IdleKick(this);
        Main.idleKick.frequency = frequency;
        Main.idleKick.warnIdle = config.getInt("warn.idle", Main.idleKick.warnIdle);
        Main.idleKick.warnPrivate = config.getString("warn.private", Main.idleKick.warnPrivate);
        Main.idleKick.warnBroadcast = config.getString("warn.broadcast", Main.idleKick.warnBroadcast);
        Main.idleKick.backBroadcast = config.getString("warn.backBroadcast", Main.idleKick.backBroadcast);
        Main.idleKick.kickIdle = config.getInt("kick.idle", Main.idleKick.kickIdle);
        Main.idleKick.kickReason = config.getString("kick.reason", Main.idleKick.kickReason);
        Main.idleKick.tracker.clearFilters();
        Main.idleKick.tracker.addFilters(filters);
        Main.idleKick.start();
    }

}
