package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.playeractivity.commands.Away;
import edgruberman.bukkit.playeractivity.commands.Back;
import edgruberman.bukkit.playeractivity.commands.Who;
import edgruberman.bukkit.playeractivity.commands.WhoDetail;
import edgruberman.bukkit.playeractivity.commands.WhoList;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.IdleKick;
import edgruberman.bukkit.playeractivity.consumers.IdleNotify;
import edgruberman.bukkit.playeractivity.consumers.ListTag;
import edgruberman.bukkit.playeractivity.dependencies.DependencyChecker;

public final class Main extends JavaPlugin {

    public static IdleNotify idleNotify = null;
    public static IdleKick idleKick = null;
    public static AwayBack awayBack = null;
    public static ListTag listTag = null;

    private static final String MINIMUM_CONFIGURATION_VERSION = "1.4.0a23";
    private ConfigurationFile configurationFile;

    private static Main that = null;

    @Override
    public void onLoad() {
        new DependencyChecker(this);
    }

    @Override
    public void onEnable() {
        Main.that = this;

        this.configurationFile = new ConfigurationFile(this);
        this.configurationFile.setMinVersion(Main.MINIMUM_CONFIGURATION_VERSION);
        this.configurationFile.load();
        this.configurationFile.setLoggingLevel();

        new Message(this);

        this.configure(this.configurationFile.getConfig());
    }

    @Override
    public void onDisable() {
        if (Main.idleKick != null) Main.idleKick.stop();
        if (Main.idleNotify != null) Main.idleNotify.stop();
        if (Main.awayBack != null) Main.awayBack.stop();
        if (Main.listTag != null) Main.listTag.stop();
        if (this.configurationFile.isSaveQueued()) this.configurationFile.save();
    }

    public void configure(final ConfigurationSection config) {
        this.loadIdleNotify(config.getConfigurationSection("idleNotify"));
        this.loadIdleKick(config.getConfigurationSection("idleKick"));
        this.loadAwayBack(config.getConfigurationSection("awayBack"));
        this.loadWho(config.getConfigurationSection("who"));
        this.loadListTag(config.getConfigurationSection("listTag"));
    }

    public static boolean enable(final String consumer) {
        final ConfigurationSection section = Main.that.configurationFile.getConfig().getConfigurationSection(consumer);
        if (section == null) return false;

        section.set("enabled", true);

        if (consumer.equalsIgnoreCase("idleKick")) Main.that.loadIdleKick(section);
        else if (consumer.equalsIgnoreCase("idleNotify")) Main.that.loadIdleNotify(section);
        else if (consumer.equalsIgnoreCase("awayBack")) Main.that.loadAwayBack(section);
        else if (consumer.equalsIgnoreCase("listTag")) Main.that.loadListTag(section);
        else return false;

        Main.that.configurationFile.save();
        return true;
    }

    private void loadIdleNotify(final ConfigurationSection section) {
        if (Main.idleNotify != null) Main.idleNotify.stop();

        if (section == null || !section.getBoolean("enabled", false)) {
            Main.idleNotify = null;
            return;
        }

        final List<Class <? extends Interpreter>> interpreters = this.findInterpreters(section.getStringList("activity"));
        if (interpreters.size() == 0) return;

        if (Main.idleNotify == null) Main.idleNotify = new IdleNotify(this);
        Main.idleNotify.idle = (long) section.getInt("idle", (int) Main.idleNotify.idle / 1000) * 1000;
        Main.idleNotify.privateFormat = section.getString("private", Main.idleNotify.privateFormat);
        Main.idleNotify.broadcast = section.getString("broadcast", Main.idleNotify.broadcast);
        Main.idleNotify.backBroadcast = section.getString("backBroadcast", Main.idleNotify.backBroadcast);
        Main.idleNotify.start(interpreters);
    }

    private void loadIdleKick(final ConfigurationSection section) {
        if (Main.idleKick != null) Main.idleKick.stop();

        if (section == null || !section.getBoolean("enabled", false)) {
            Main.idleKick = null;
            return;
        }

        final List<Class <? extends Interpreter>> interpreters = this.findInterpreters(section.getStringList("activity"));
        if (interpreters.size() == 0) return;

        if (Main.idleKick == null) Main.idleKick = new IdleKick(this);
        Main.idleKick.idle = (long) section.getInt("idle", (int) Main.idleKick.idle / 1000) * 1000;
        Main.idleKick.reason = section.getString("reason", Main.idleKick.reason);
        Main.idleKick.start(interpreters);
    }

    private void loadAwayBack(final ConfigurationSection section) {
        if (Main.awayBack != null) Main.awayBack.stop();

        if (section == null || !section.getBoolean("enabled", false)) {
            Main.awayBack = null;
            return;
        }

        if (Main.awayBack == null) Main.awayBack = new AwayBack(this);
        Main.awayBack.overrideIdle = section.getBoolean("overrideIdle", Main.awayBack.overrideIdle);
        Main.awayBack.awayFormat = section.getString("away", Main.awayBack.awayFormat);
        Main.awayBack.backFormat = section.getString("back", Main.awayBack.backFormat);
        Main.awayBack.defaultReason = section.getString("reason", Main.awayBack.defaultReason);
        Main.awayBack.mentionsFormat = section.getString("mentions", Main.awayBack.mentionsFormat);

        final List<Class <? extends Interpreter>> interpreters = this.findInterpreters(section.getStringList("activity"));
        if (interpreters.size() == 0) return;

        Main.awayBack.start(interpreters);

        new Away(this);
        new Back(this);
    }

    private void loadWho(final ConfigurationSection section) {
        if (section == null || !section.getBoolean("enabled", false)) return;
        // TODO unregister command if disabled

        WhoList.format = section.getString("list.format", WhoList.format);
        WhoList.delimiter = section.getString("list.delimiter", WhoList.delimiter);
        WhoList.name = section.getString("list.name", WhoList.name);
        WhoList.away = section.getString("list.away", WhoList.away);
        WhoList.idle = section.getString("list.idle", WhoList.idle);

        WhoDetail.connected = section.getString("detail.connected", WhoDetail.connected);
        WhoDetail.away = section.getString("detail.away", WhoDetail.away);
        WhoDetail.idle = section.getString("detail.idle", WhoDetail.idle);
        WhoDetail.disconnected = section.getString("detail.disconnected", WhoDetail.disconnected);

        new Who(this);
    }

    private void loadListTag(final ConfigurationSection section) {
        if (Main.listTag != null) Main.listTag.stop();

        if (section == null || !section.getBoolean("enabled", false)) {
            Main.listTag = null;
            return;
        }

        final List<Class <? extends Interpreter>> interpreters = this.findInterpreters(section.getStringList("activity"));
        if (interpreters.size() == 0) return;

        if (Main.listTag == null) Main.listTag = new ListTag(this);
        Main.listTag.idle = (long) section.getInt("idle", (int) Main.listTag.idle / 1000) * 1000;
        Main.listTag.awayTag = section.getString("awayTag", Main.listTag.awayTag);
        Main.listTag.idleTag = section.getString("idleTag", Main.listTag.idleTag);
        Main.listTag.bedTag = section.getString("bedTag", Main.listTag.bedTag);
        Main.listTag.start(interpreters);
    }

    private List<Class <? extends Interpreter>> findInterpreters(final List<String> classNames) {
        final List<Class <? extends Interpreter>> interpreters = new ArrayList<Class <? extends Interpreter>>();
        for (final String className : classNames) {
            final Class <? extends Interpreter> interpreter = EventTracker.findInterpreter(className);
            if (interpreter == null) {
                this.getLogger().log(Level.WARNING, "Unsupported activity: " + className);
                continue;
            }

            interpreters.add(interpreter);
        }
        return interpreters;
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
