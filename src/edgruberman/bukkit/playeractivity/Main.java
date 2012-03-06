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
        final FileConfiguration config = this.configurationFile.load();
        this.loadIdleKick(config.getConfigurationSection("IdleKick"));
    }

    private void loadIdleKick(final ConfigurationSection config) {
        if (Main.idleKick != null) {
            Main.idleKick.stop();
            if (Main.idleKick.tracker != null) Main.idleKick.tracker.clear();
        }

        if (!config.getBoolean("enabled", false)) return;

        final Long frequency = config.getLong("frequency");
        if (frequency == null || frequency <= 0) return;

        final List<Interpreter> interpreters = new ArrayList<Interpreter>();
        for (final String className : config.getStringList("activity")) {
            final Interpreter interpreter = EventTracker.newInterpreter(className);
            if (interpreter == null) {
                Main.messageManager.log("Unsupported activity: " + className, MessageLevel.WARNING);
                continue;
            }

            interpreters.add(interpreter);
        }
        if (interpreters.size() == 0) return;

        if (Main.idleKick == null) Main.idleKick = new IdleKick(this);
        Main.idleKick.frequency = frequency;
        Main.idleKick.warnIdle = config.getInt("warn.idle", Main.idleKick.warnIdle);
        Main.idleKick.warnPrivate = config.getString("warn.private", Main.idleKick.warnPrivate);
        Main.idleKick.warnBroadcast = config.getString("warn.broadcast", Main.idleKick.warnBroadcast);
        Main.idleKick.backBroadcast = config.getString("warn.backBroadcast", Main.idleKick.backBroadcast);
        Main.idleKick.kickIdle = config.getInt("kick.idle", Main.idleKick.kickIdle);
        Main.idleKick.kickReason = config.getString("kick.reason", Main.idleKick.kickReason);
        Main.idleKick.tracker.addInterpreters(interpreters);
        Main.idleKick.start();
    }

}
