package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.playeractivity.commands.Away;
import edgruberman.bukkit.playeractivity.commands.Back;
import edgruberman.bukkit.playeractivity.commands.Who;
import edgruberman.bukkit.playeractivity.commands.WhoDetail;
import edgruberman.bukkit.playeractivity.commands.WhoList;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.IdleKick;
import edgruberman.bukkit.playeractivity.consumers.IdleNotify;
import edgruberman.bukkit.playeractivity.dependencies.DependencyChecker;

public final class Main extends JavaPlugin {

    public static IdleNotify idleNotify = null;
    public static IdleKick idleKick = null;
    public static AwayBack awayBack = null;

    private static final String MINIMUM_CONFIGURATION_VERSION = "1.3.0b19";
    private ConfigurationFile configurationFile;

    @Override
    public void onLoad() {
        new DependencyChecker(this);
    }

    @Override
    public void onEnable() {
        this.configurationFile = new ConfigurationFile(this);
        this.configurationFile.setMinVersion(Main.MINIMUM_CONFIGURATION_VERSION);
        this.configurationFile.load();
        this.setLoggingLevel();

        new Message(this);

        this.configure();
    }

    @Override
    public void onDisable() {
        if (Main.idleKick != null) Main.idleKick.stop();
        if (this.configurationFile.isSaveQueued()) this.configurationFile.save();
    }

    private void setLoggingLevel() {
        final String name = this.configurationFile.getConfig().getString("logLevel", "INFO");
        Level level;
        try { level = Level.parse(name); } catch (final Exception e) {
            level = Level.INFO;
            this.getLogger().warning("Unrecognized java.util.logging.Level in \"" + this.configurationFile.getFile().getPath() + "\"; logLevel: " + name);
        }

        // Only set the parent handler lower if necessary, otherwise leave it alone for other configurations that have set it.
        for (final Handler h : this.getLogger().getParent().getHandlers())
            if (h.getLevel().intValue() > level.intValue()) h.setLevel(level);

        this.getLogger().setLevel(level);
        this.getLogger().log(Level.CONFIG, "Logging level set to: " + this.getLogger().getLevel());
    }

    public void configure() {
        final FileConfiguration config = this.configurationFile.getConfig();
        this.loadIdleNotify(config.getConfigurationSection("idleNotify"));
        this.loadIdleKick(config.getConfigurationSection("idleKick"));
        this.loadAwayBack(config.getConfigurationSection("awayBack"));
        this.loadWho(config.getConfigurationSection("who"));
    }

    private void loadIdleNotify(final ConfigurationSection section) {
        if (Main.idleNotify != null) Main.idleNotify.stop();

        if (section == null || !section.getBoolean("enabled", false)) return;

        final List<Class <? extends Interpreter>> interpreters = new ArrayList<Class <? extends Interpreter>>();
        for (final String className : section.getStringList("activity")) {
            final Class <? extends Interpreter> interpreter = EventTracker.findInterpreter(className);
            if (interpreter == null) {
                this.getLogger().log(Level.WARNING, "Unsupported IdleNotify.activity: " + className);
                continue;
            }

            interpreters.add(interpreter);
        }
        if (interpreters.size() == 0) return;

        if (Main.idleNotify == null) Main.idleNotify = new IdleNotify(this);
        Main.idleNotify.idle = (long) section.getInt("idle", (int) Main.idleNotify.idle / 1000) * 1000;
        Main.idleNotify.privateFormat = section.getString("private", Main.idleNotify.privateFormat);
        Main.idleNotify.broadcast = section.getString("broadcast", Main.idleNotify.broadcast);
        Main.idleNotify.backBroadcast = section.getString("backBroadcast", Main.idleNotify.backBroadcast);
        Main.idleNotify.awayBroadcastOverride = section.getBoolean("awayBroadcastOverride", Main.idleNotify.awayBroadcastOverride);
        Main.idleNotify.start(interpreters);
    }

    private void loadIdleKick(final ConfigurationSection section) {
        if (Main.idleKick != null) Main.idleKick.stop();

        if (section == null || !section.getBoolean("enabled", false)) return;

        final List<Class <? extends Interpreter>> interpreters = new ArrayList<Class <? extends Interpreter>>();
        for (final String className : section.getStringList("activity")) {
            final Class <? extends Interpreter> interpreter = EventTracker.findInterpreter(className);
            if (interpreter == null) {
                this.getLogger().log(Level.WARNING, "Unsupported IdleKick.activity: " + className);
                continue;
            }

            interpreters.add(interpreter);
        }
        if (interpreters.size() == 0) return;

        if (Main.idleKick == null) Main.idleKick = new IdleKick(this);
        Main.idleKick.idle = (long) section.getInt("idle", (int) Main.idleKick.idle / 1000) * 1000;
        Main.idleKick.reason = section.getString("reason", Main.idleKick.reason);
        Main.idleKick.start(interpreters);
    }

    private void loadAwayBack(final ConfigurationSection section) {
        if (Main.awayBack != null) Main.awayBack.stop();

        if (section == null || !section.getBoolean("enabled", false)) return;

        if (Main.awayBack == null) Main.awayBack = new AwayBack(this);
        Main.awayBack.awayFormat = section.getString("away", Main.awayBack.awayFormat);
        Main.awayBack.backFormat = section.getString("back", Main.awayBack.backFormat);
        Main.awayBack.defaultReason = section.getString("reason", Main.awayBack.defaultReason);

        new Away(this);
        new Back(this);

        final List<Class <? extends Interpreter>> interpreters = new ArrayList<Class <? extends Interpreter>>();
        for (final String className : section.getStringList("activity")) {
            final Class <? extends Interpreter> interpreter = EventTracker.findInterpreter(className);
            if (interpreter == null) {
                this.getLogger().log(Level.WARNING, "Unsupported Away.activity: " + className);
                continue;
            }

            interpreters.add(interpreter);
        }
        if (interpreters.size() == 0) return;

        Main.awayBack.start(interpreters);
    }

    private void loadWho(final ConfigurationSection section) {
        if (section == null || !section.getBoolean("enabled", false)) return;

        new Who(this);

        WhoList.format = section.getString("list.format", WhoList.format);
        WhoList.delimiter = section.getString("list.delimiter", WhoList.delimiter);
        WhoList.name = section.getString("list.name", WhoList.name);
        WhoList.away = section.getString("list.away", WhoList.away);
        WhoList.idle = section.getString("list.idle", WhoList.idle);

        WhoDetail.connected = section.getString("detail.connected", WhoDetail.connected);
        WhoDetail.away = section.getString("detail.away", WhoDetail.away);
        WhoDetail.idle = section.getString("detail.idle", WhoDetail.idle);
        WhoDetail.disconnected = section.getString("detail.disconnected", WhoDetail.disconnected);
    }

    public static String duration(final long total) {
        final long totalSeconds = total / 1000;
        final long days = totalSeconds / 86400;
        final long hours = (totalSeconds % 86400) / 3600;
        final long minutes = ((totalSeconds % 86400) % 3600) / 60;
        final long seconds = totalSeconds % 60;
        final StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(Long.toString(days)).append("d");
        if (hours > 0) sb.append((sb.length() > 0) ? " " : "").append(Long.toString(hours)).append("h");
        if (minutes > 0) sb.append((sb.length() > 0) ? " " : "").append(Long.toString(minutes)).append("m");
        if (seconds > 0) sb.append((sb.length() > 0) ? " " : "").append(Long.toString(seconds)).append("s");
        return sb.toString();
    }

}
