package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;
import edgruberman.bukkit.playeractivity.commands.Away;
import edgruberman.bukkit.playeractivity.commands.Back;
import edgruberman.bukkit.playeractivity.commands.Who;
import edgruberman.bukkit.playeractivity.commands.WhoDetail;
import edgruberman.bukkit.playeractivity.commands.WhoList;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.IdleKick;

public final class Main extends JavaPlugin {

    public static MessageManager messageManager;
    public static IdleKick idleKick = null;
    public static AwayBack awayBack = null;

    private static final String MINIMUM_CONFIGURATION_VERSION = "1.3.0b3";
    private ConfigurationFile configurationFile;

    @Override
    public void onEnable() {
        this.configurationFile = new ConfigurationFile(this);
        this.configurationFile.setMinVersion(Main.MINIMUM_CONFIGURATION_VERSION);
        this.configurationFile.load();
        this.setLoggingLevel();

        Main.messageManager = new MessageManager(this);

        this.configure();

        new Who(this);
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

        // Only set the parent handler lower if necessary, otherwise leave it alone for other configurations that have set it.
        for (final Handler h : this.getLogger().getParent().getHandlers())
            if (h.getLevel().intValue() > level.intValue()) h.setLevel(level);

        this.getLogger().setLevel(level);
        this.getLogger().log(Level.CONFIG, "Logging level set to: " + this.getLogger().getLevel());
    }

    public void configure() {
        final FileConfiguration config = this.configurationFile.getConfig();
        this.loadIdleKick(config.getConfigurationSection("idleKick"));
        this.loadAwayBack(config.getConfigurationSection("awayBack"));
        this.loadWho(config.getConfigurationSection("who"));
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
        Main.idleKick.warnIdle = (long) section.getInt("warn.idle", (int) Main.idleKick.warnIdle / 1000) * 1000;
        Main.idleKick.warnPrivate = section.getString("warn.private", Main.idleKick.warnPrivate);
        Main.idleKick.warnBroadcast = section.getString("warn.broadcast", Main.idleKick.warnBroadcast);
        Main.idleKick.backBroadcast = section.getString("warn.backBroadcast", Main.idleKick.backBroadcast);
        Main.idleKick.awayBroadcastOverride = section.getBoolean("warn.awayBroadcastOverride", Main.idleKick.awayBroadcastOverride);
        Main.idleKick.kickIdle = (long) section.getInt("kick.idle", (int) Main.idleKick.kickIdle / 1000) * 1000;
        Main.idleKick.kickReason = section.getString("kick.reason", Main.idleKick.kickReason);
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
        if (section == null) return;

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
