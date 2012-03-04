package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;

public final class Main extends JavaPlugin {

    public static MessageManager messageManager;

    private ConfigurationFile configurationFile;
    private Tracker tracker = null;

    @Override
    public void onLoad() {
        Main.messageManager = new MessageManager(this);
        Main.messageManager.log("Version " + this.getDescription().getVersion());
        this.configurationFile = new ConfigurationFile(this);
    }

    @Override
    public void onEnable() {
        this.loadConfiguration();
        Main.messageManager.log("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        if (this.configurationFile.isSaveQueued()) this.configurationFile.save();
        this.getServer().getScheduler().cancelTasks(this);
        Main.messageManager.log("Plugin Disabled");
    }

    public void loadConfiguration() {
        final FileConfiguration config = this.configurationFile.load();

        // TODO adjust tracker frequency and listeners if modified in configuration file
        if (this.tracker == null) {
            final List<Class<? extends EventListener>> listeners = new ArrayList<Class<? extends EventListener>>();
            for (final String reference : config.getStringList("activity")) {
                Class<? extends EventListener> listener;
                try {
                    listener = Class.forName("edgruberman.bukkit.playeractivity.listeners." + reference).asSubclass(EventListener.class);
                } catch (final ClassNotFoundException e) {
                    Main.messageManager.log("Unsupported Listener: " + reference, MessageLevel.WARNING);
                    continue;
                }
                listeners.add(listener);
            }

            this.tracker = new Tracker(this, config.getLong("frequency"), listeners);
        }

        this.tracker.warnIdle = config.getInt("warn.idle");
        this.tracker.warnPrivate = config.getString("warn.private", this.tracker.warnPrivate);
        this.tracker.warnBroadcast = config.getString("warn.broadcast", this.tracker.warnBroadcast);
        this.tracker.backBroadcast = config.getString("back.broadcast", this.tracker.backBroadcast);
        this.tracker.kickIdle = config.getInt("kick.idle");
        this.tracker.kickReason = config.getString("kick.reason", this.tracker.kickReason);
    }

}
