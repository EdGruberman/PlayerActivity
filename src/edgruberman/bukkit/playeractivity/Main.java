package edgruberman.bukkit.playeractivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.playeractivity.commands.Away;
import edgruberman.bukkit.playeractivity.commands.Back;
import edgruberman.bukkit.playeractivity.commands.Reload;
import edgruberman.bukkit.playeractivity.commands.Who;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.IdleKick;
import edgruberman.bukkit.playeractivity.consumers.IdleNotify;
import edgruberman.bukkit.playeractivity.consumers.ListTag;
import edgruberman.bukkit.playeractivity.messaging.couriers.ConfigurationCourier;
import edgruberman.bukkit.playeractivity.messaging.couriers.TimestampedConfigurationCourier;

public final class Main extends JavaPlugin {

    private static final Version MINIMUM_CONFIGURATION = new Version("3.0.0b11");

    public IdleNotify idleNotify = null;
    public IdleKick idleKick = null;
    public AwayBack awayBack = null;
    public ListTag listTag = null;

    private ConfigurationCourier courier;

    @Override
    public void onEnable() {
        this.reloadConfig();
        this.courier = new TimestampedConfigurationCourier(this, "messages");

        if (this.getConfig().getBoolean("idleNotify.enabled"))
            this.idleNotify = new IdleNotify(this, this.getConfig().getConfigurationSection("idleNotify"), this.courier, "playeractivity.idle.ignore.notify");

        if (this.getConfig().getBoolean("idleKick.enabled")) {
            this.idleKick = new IdleKick(this, this.getConfig().getConfigurationSection("idleKick"), this.courier, "playeractivity.idle.ignore.kick");
            if (this.idleNotify != null) this.idleNotify.idleKick = this.idleKick;
        }

        if (this.getConfig().getBoolean("listTag.enabled"))
            this.listTag = new ListTag(this, this.getConfig().getConfigurationSection("listTag"), this.courier, "playeractivity.idle.ignore.listtag");

        if (this.getConfig().getBoolean("awayBack.enabled")) {
            this.awayBack = new AwayBack(this, this.getConfig().getConfigurationSection("awayBack"), this.courier);
            if (this.awayBack.overrideIdle) {
                this.awayBack.idleNotify = this.idleNotify;
                if (this.idleNotify != null) this.idleNotify.awayBack = this.awayBack;
            }
            if (this.listTag != null) this.listTag.awayBack = this.awayBack;
            this.getCommand("playeractivity:away").setExecutor(new Away(this.courier, this.awayBack));
            this.getCommand("playeractivity:back").setExecutor(new Back(this.courier, this.awayBack));
        }

        this.getCommand("playeractivity:who").setExecutor(new Who(this, this.courier, this.awayBack, this.idleNotify, this.listTag));
        this.getCommand("playeractivity:reload").setExecutor(new Reload(this, this.courier));
    }

    @Override
    public void onDisable() {
        if (this.idleNotify != null) this.idleNotify.unload();
        if (this.idleKick != null) this.idleKick.unload();
        if (this.awayBack != null) this.awayBack.unload();
        if (this.listTag != null) this.listTag.unload();
        this.courier = null;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        this.courier.send(sender, "commandDisabled", label);
        return true;
    }

    @Override
    public void reloadConfig() {
        this.saveDefaultConfig();
        super.reloadConfig();
        this.setLogLevel(this.getConfig().getString("logLevel"));

        final Version version = new Version(this.getConfig().isSet("version") ? this.getConfig().getString("version") : null);
        if (version.compareTo(Main.MINIMUM_CONFIGURATION) >= 0) return;

        this.archiveConfig("config.yml", version);
        this.saveDefaultConfig();
        this.reloadConfig();
    }

    @Override
    public void saveDefaultConfig() {
        this.extractConfig("config.yml", false);
    }

    private void archiveConfig(final String resource, final Version version) {
        final String backupName = "%1$s - Archive version %2$s - %3$tY%3$tm%3$tdT%3$tH%3$tM%3$tS.yml";
        final File backup = new File(this.getDataFolder(), String.format(backupName, resource.replaceAll("(?i)\\.yml$", ""), version, new Date()));
        final File existing = new File(this.getDataFolder(), resource);

        if (!existing.renameTo(backup))
            throw new IllegalStateException("Unable to archive configuration file \"" + existing.getPath() + "\" with version \"" + version + "\" to \"" + backup.getPath() + "\"");

        this.getLogger().warning("Archived configuration file \"" + existing.getPath() + "\" with version \"" + version + "\" to \"" + backup.getPath() + "\"");
    }

    private void extractConfig(final String resource, final boolean replace) {
        final Charset source = Charset.forName("UTF-8");
        final Charset target = Charset.defaultCharset();
        if (target.equals(source)) {
            super.saveResource(resource, replace);
            return;
        }

        final File config = new File(this.getDataFolder(), resource);
        if (config.exists()) return;

        final char[] cbuf = new char[1024]; int read;
        try {
            final Reader in = new BufferedReader(new InputStreamReader(this.getResource(resource), source));
            final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config), target));
            while((read = in.read(cbuf)) > 0) out.write(cbuf, 0, read);
            out.close(); in.close();

        } catch (final Exception e) {
            throw new IllegalArgumentException("Could not extract configuration file \"" + resource + "\" to " + config.getPath() + "\";" + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void setLogLevel(final String name) {
        Level level;
        try { level = Level.parse(name); } catch (final Exception e) {
            level = Level.INFO;
            this.getLogger().warning("Log level defaulted to " + level.getName() + "; Unrecognized java.util.logging.Level: " + name);
        }

        // only set the parent handler lower if necessary, otherwise leave it alone for other configurations that have set it
        for (final Handler h : this.getLogger().getParent().getHandlers())
            if (h.getLevel().intValue() > level.intValue()) h.setLevel(level);

        this.getLogger().setLevel(level);
        this.getLogger().config("Log level set to: " + this.getLogger().getLevel());
    }

    public static String readableDuration(final long ms) {
        long total = TimeUnit.MILLISECONDS.toSeconds(ms);

        final long seconds = total % 60;
        total /= 60;
        final long minutes = total % 60;
        total /= 60;
        final long hours = total % 24;
        total /= 24;
        final long days = total;

        final StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(Long.toString(days)).append("d");
        if (hours > 0) sb.append((sb.length() > 0) ? " " : "").append(Long.toString(hours)).append("h");
        if (minutes > 0) sb.append((sb.length() > 0) ? " " : "").append(Long.toString(minutes)).append("m");
        if (seconds > 0) sb.append((sb.length() > 0) ? " " : "").append(Long.toString(seconds)).append("s");
        return sb.toString();
    }

}
