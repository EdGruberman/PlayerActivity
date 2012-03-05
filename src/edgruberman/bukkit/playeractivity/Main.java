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

    private ConfigurationFile configurationFile;
    private IdleKick idleKick = null;

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
        if (this.idleKick != null) this.idleKick.stop();
        final FileConfiguration config = this.configurationFile.load();
        this.loadIdleKick(config.getConfigurationSection("IdleKick"));
    }

    private void loadIdleKick(final ConfigurationSection config) {
        if (!config.getBoolean("enabled", false)) return;

        final Long frequency = config.getLong("frequency");
        if (frequency == null || frequency <= 0) return;

        final List<Class<? extends EventListener>> listeners = new ArrayList<Class<? extends EventListener>>();
        for (final String reference : config.getStringList("activity")) {
            Class<? extends EventListener> listener;
            try {
                listener = Class.forName("edgruberman.bukkit.playeractivity.listeners." + reference + "Listener").asSubclass(EventListener.class);
            } catch (final ClassNotFoundException e) {
                Main.messageManager.log("Unsupported Listener: " + reference, MessageLevel.WARNING);
                continue;
            }
            listeners.add(listener);
        }
        if (listeners.size() == 0) return;

        this.idleKick = new IdleKick(this, frequency, listeners);
        this.idleKick.warnIdle = config.getInt("warn.idle", this.idleKick.warnIdle);
        this.idleKick.warnPrivate = config.getString("warn.private", this.idleKick.warnPrivate);
        this.idleKick.warnBroadcast = config.getString("warn.broadcast", this.idleKick.warnBroadcast);
        this.idleKick.backBroadcast = config.getString("warn.backBroadcast", this.idleKick.backBroadcast);
        this.idleKick.kickIdle = config.getInt("kick.idle", this.idleKick.kickIdle);
        this.idleKick.kickReason = config.getString("kick.reason", this.idleKick.kickReason);
        this.idleKick.start();
    }

}
