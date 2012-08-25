package edgruberman.bukkit.playeractivity.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author EdGruberman (ed@rjump.com)
 * @version 1.0.1
 */
public class CustomPlugin extends JavaPlugin {

    public static final Charset CONFIGURATION_SOURCE = Charset.forName("UTF-8");
    public static final String CONFIGURATION_ARCHIVE = "{0} - Archive version {1} - {2,date,yyyyMMddHHmmss}.yml"; // 0 = Name, 1 = Version, 2 = Date
    public static final String CONFIGURATION_FILE = "config.yml";
    public static final Level DEFAULT_LOG = Level.INFO;

    /** minimum version required for configuration files; indexed by relative file name (e.g. "config.yml") */
    private final Map<String, Version> configurationMinimums = new HashMap<String, Version>();
    private FileConfiguration config = null;
    private char pathSeparator = '.';

    public void putConfigMinimum(final String resource, final String version) {
        this.configurationMinimums.put(resource, new Version(version));
    }

    public CustomPlugin setPathSeparator(final char separator) {
        this.pathSeparator = separator;
        return this;
    }

    @Override
    public FileConfiguration getConfig() {
        if (this.config == null) this.reloadConfig();

        return this.config;
    }

    @Override
    public void reloadConfig() {
        this.config = this.loadConfig(CustomPlugin.CONFIGURATION_FILE, this.pathSeparator, this.configurationMinimums.get(CustomPlugin.CONFIGURATION_FILE));
        super.reloadConfig();
        this.setLogLevel(this.getConfig().getString("logLevel"));
    }

    @Override
    public void saveDefaultConfig() {
        this.extractConfig(CustomPlugin.CONFIGURATION_FILE, false);
    }

    public FileConfiguration loadConfig(final String resource) {
        return this.loadConfig(resource, this.pathSeparator, this.configurationMinimums.get(resource));
    }

    public FileConfiguration loadConfig(final String resource, final char pathSeparator, final Version required) {
        // extract default if not existing
        this.extractConfig(resource, false);

        final File existing = new File(this.getDataFolder(), resource);
        final YamlConfiguration yaml = new YamlConfiguration();
        yaml.options().pathSeparator(pathSeparator);
        try { yaml.load(existing);
        } catch (final Exception e) { this.getLogger().severe("Unable to load configuration file: " + existing.getPath() + "; " + e); }
        if (required == null) return yaml;

        // verify required or later version
        final Version version = new Version(yaml.getString("version"));
        if (version.compareTo(required) >= 0) return yaml;

        this.archiveConfig(resource, version);

        // extract default and reload
        return this.loadConfig(resource, this.pathSeparator, null);
    }

    public void extractConfig(final String resource, final boolean replace) {
        final Charset target = Charset.defaultCharset();
        if (target.equals(CustomPlugin.CONFIGURATION_SOURCE)) {
            super.saveResource(resource, replace);
            return;
        }

        final File config = new File(this.getDataFolder(), resource);
        if (config.exists()) return;

        config.getParentFile().mkdirs();

        final char[] cbuf = new char[1024]; int read;
        try {
            final Reader in = new BufferedReader(new InputStreamReader(this.getResource(resource), CustomPlugin.CONFIGURATION_SOURCE));
            final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config), target));
            while((read = in.read(cbuf)) > 0) out.write(cbuf, 0, read);
            out.close(); in.close();

        } catch (final Exception e) {
            throw new IllegalArgumentException("Could not extract configuration file \"" + resource + "\" to " + config.getPath() + "\";" + e);
        }
    }

    public void archiveConfig(final String resource, final Version version) {
        final File backup = new File(this.getDataFolder(), MessageFormat.format(CustomPlugin.CONFIGURATION_ARCHIVE, resource.replaceAll("(?i)\\.yml$", ""), version, new Date()));
        final File existing = new File(this.getDataFolder(), resource);

        if (!existing.renameTo(backup))
            throw new IllegalStateException("Unable to archive configuration file \"" + existing.getPath() + "\" with version \"" + version + "\" to \"" + backup.getPath() + "\"");

        this.getLogger().warning("Archived configuration file \"" + existing.getPath() + "\" with version \"" + version + "\" to \"" + backup.getPath() + "\"");
    }

    public void setLogLevel(final String name) {
        Level level;
        try { level = Level.parse(name); } catch (final Exception e) {
            level = CustomPlugin.DEFAULT_LOG;
            this.getLogger().warning("Log level defaulted to " + level.getName() + "; Unrecognized java.util.logging.Level: " + name + "; " + e);
        }

        // only set the parent handler lower if necessary, otherwise leave it alone for other configurations that have set it
        for (final Handler h : this.getLogger().getParent().getHandlers())
            if (h.getLevel().intValue() > level.intValue()) h.setLevel(level);

        this.getLogger().setLevel(level);
        this.getLogger().log(Level.CONFIG, "Log level set to: {0} ({1})"
                , new Object[] { this.getLogger().getLevel(), this.getLogger().getLevel().intValue() });
    }

}
