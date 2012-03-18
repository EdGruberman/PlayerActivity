package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;
import edgruberman.bukkit.playeractivity.consumers.IdleKick;

public final class Main extends JavaPlugin {

    public static MessageManager messageManager;
    public static IdleKick idleKick = null;

    private static final String MINIMUM_CONFIGURATION_VERSION = "1.1.0";
    private ConfigurationFile configurationFile;
    private boolean firstEnable = true;

    @Override
    public void onLoad() {
        this.configurationFile = new ConfigurationFile(this);
        this.configurationFile.setMinVersion(Main.MINIMUM_CONFIGURATION_VERSION);
        this.configurationFile.load();
        this.setLoggingLevel();
        Main.messageManager = new MessageManager(this);
    }

    @Override
    public void onEnable() {
        this.loadConfiguration();
        this.firstEnable = false;
    }

    @Override
    public void onDisable() {
        if (Main.idleKick != null) Main.idleKick.stop();
        if (this.configurationFile.isSaveQueued()) this.configurationFile.save();
    }

    private void setLoggingLevel() {
        final String name = this.configurationFile.getConfig().getString("logLevel", "INFO");
        Level level = MessageLevel.parse(name);
        if (level == null) level = Level.INFO;
        this.getServer().getLogger().setLevel(level);
    }

    public void loadConfiguration() {
        if (!this.firstEnable) this.configurationFile.load();
        final FileConfiguration config = this.configurationFile.getConfig();
        this.loadIdleKick(config.getConfigurationSection("IdleKick"));
    }

    private void loadIdleKick(final ConfigurationSection config) {
        if (Main.idleKick != null) Main.idleKick.stop();

        if (!config.getBoolean("enabled", false)) return;

        final List<Class <? extends Interpreter>> interpreters = new ArrayList<Class <? extends Interpreter>>();
        for (final String className : config.getStringList("activity")) {
            final Class <? extends Interpreter> interpreter = EventTracker.findInterpreter(className);
            if (interpreter == null) {
                this.getLogger().log(Level.WARNING, "Unsupported activity: " + className);
                continue;
            }

            interpreters.add(interpreter);
        }
        if (interpreters.size() == 0) return;

        if (Main.idleKick == null) Main.idleKick = new IdleKick(this);
        Main.idleKick.warnIdle = (long) config.getInt("warn.idle", (int) Main.idleKick.warnIdle / 1000) * 1000;
        Main.idleKick.warnPrivate = config.getString("warn.private", Main.idleKick.warnPrivate);
        Main.idleKick.warnBroadcast = config.getString("warn.broadcast", Main.idleKick.warnBroadcast);
        Main.idleKick.backBroadcast = config.getString("warn.backBroadcast", Main.idleKick.backBroadcast);
        Main.idleKick.kickIdle = (long) config.getInt("kick.idle", (int) Main.idleKick.kickIdle / 1000) * 1000;
        Main.idleKick.kickReason = config.getString("kick.reason", Main.idleKick.kickReason);
        Main.idleKick.start(interpreters);
    }

}
