package edgruberman.bukkit.playeractivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Standardized plugin configuration file management class.
 *
 * Defaults will be extracted from the JAR.  Save requests can be queued to
 * avoid performance penalties for too many save requests occurring too
 * frequently.
 */
public final class ConfigurationFile {

    /**
     * Standard plugin configuration file.
     */
    private static final String DEFAULT_FILE = "config.yml";

    /**
     * Path in JAR to find files containing default values.
     */
    private static final String DEFAULT_PATH = "/defaults/";

    /**
     * Default maximum save frequency.
     */
    private static final int DEFAULT_SAVE = -1; // No limit

    /**
     * Default minimum version required.
     */
    private static final String DEFAULT_MINIMUM_VERSION = null; // No minimum version required

    /**
     * Clock ticks per second; Used to determine durations between saves.
     */
    private static final int TICKS_PER_SECOND = 20;

    private final Plugin owner;
    private final File file;
    private final String defaults;
    private int maxSaveFrequency;
    private FileVersion minVersion;
    private FileConfiguration config = null;
    private Long lastSaveAttempt = null;
    private Integer taskSave = null;

    /**
     * Construct configuration file reference for standardized load and save
     * management.  (config.yml assumed.  Defaults assumed to be in
     * /defaults/config.yml.  No restrictions on how frequent saves can occur.)
     *
     * @param owner plugin that owns this configuration file
     */
    ConfigurationFile(final Plugin owner) {
        this(owner, (String) null);
    }

    /**
     * Construct configuration file reference for standardized load and save
     * management.  (config.yml assumed.  Defaults assumed to be in
     * /defaults/config.yml.)
     *
     * @param owner plugin that owns this configuration file
     * @param maxSaveFrequency shortest duration in seconds each save can occur
     */
    ConfigurationFile(final Plugin owner, final int maxSaveFrequency) {
        this(owner, (String) null, (String) null, (String) null, maxSaveFrequency);
    }

    /**
     * Construct configuration file reference for standardized load and save
     * management.  (Defaults assumed to be in /defaults/file.  No restrictions
     * on how frequent saves can occur.)
     *
     * @param owner plugin that owns this configuration file
     * @param file name of file in the default data directory
     */
    ConfigurationFile(final Plugin owner, final String file) {
        this(owner, file, (String) null);
    }

    /**
     * Construct configuration file reference for standardized load and save
     * management.  (No restrictions on how frequent saves can occur.)
     *
     * @param owner plugin that owns this configuration file
     * @param file name of file in the default data directory
     * @param defaults path to default configuration file supplied in JAR
     */
    ConfigurationFile(final Plugin owner, final String file, final String defaults) {
        this(owner, file, defaults, (String) null, (Integer) null);
    }

    /**
     * Construct configuration file reference for standardized load and save
     * management.
     *
     * @param owner plugin that owns this configuration file
     * @param file name of file in the default data directory
     * @param defaults path to default configuration file supplied in JAR
     * @param minVersion minimum required version that if not met the defaults will override with
     * @param maxSaveFrequency shortest duration in seconds each save can occur
     */
    ConfigurationFile(final Plugin owner, final String file, final String defaults, final String minVersion, final Integer maxSaveFrequency) {
        this.owner = owner;

        this.file = new File(this.owner.getDataFolder(), (file != null ? file : ConfigurationFile.DEFAULT_FILE));
        this.defaults = (defaults != null ? defaults : ConfigurationFile.DEFAULT_PATH + this.file.getName());
        this.minVersion = new FileVersion(minVersion != null ? minVersion : ConfigurationFile.DEFAULT_MINIMUM_VERSION);
        this.maxSaveFrequency = (maxSaveFrequency != null ? maxSaveFrequency : ConfigurationFile.DEFAULT_SAVE);
    }


    public int getMaxSaveFrequency() {
        return this.maxSaveFrequency;
    }

    public void setMaxSaveFrequency(final int frequency) {
        this.maxSaveFrequency = frequency;
    }

    public FileVersion getMinVersion() {
        return this.minVersion;
    }

    public void setMinVersion(final String minVersion) {
        this.minVersion = new FileVersion(minVersion);
    }

    /**
     * Loads the configuration file from owning plugin's data folder.  Defaults
     * are pulled from a file embedded in the JAR.
     */
    public FileConfiguration load() {
        // Flush any pending save requests first to avoid losing any previous edits not yet committed
        if (this.isSaveQueued()) this.save();

        // Use existing file
        this.config = YamlConfiguration.loadConfiguration(this.file);
        if (this.file.exists()) {
            if (this.getVersion().compareTo(this.minVersion) >= 0) return this.config;

            // Backup existing file
            String backupName = this.file.getName().substring(0, this.file.getName().lastIndexOf("."));
            backupName += " backup - version " + this.getVersion().toString() + " - " + new SimpleDateFormat("yyyyMMdd'T'HHmm").format(new Date()) + ".yml";
            final File backup = new File(this.file.getParentFile(), backupName);
            this.owner.getLogger().log(Level.WARNING, "Existing configuration file \"" + this.file.getPath() + "\" with version \"" + this.getVersion() + "\" is out of date; Required minimum version is \"" + this.minVersion + "\"; Backing up existing file to \"" + backup.getPath() + "\"");
            this.file.renameTo(backup);

            // Clear out-dated configuration
            this.config = new YamlConfiguration();
        }

        // Load defaults supplied in JAR
        final InputStream defaults = (this.defaults != null ? this.owner.getClass().getResourceAsStream(this.defaults) : null);
        if (defaults != null) {
            this.config.setDefaults(YamlConfiguration.loadConfiguration(defaults));
            this.config.options().copyDefaults(true);
            this.save();

            this.config = YamlConfiguration.loadConfiguration(this.file);
            return this.config;
        }

        // No file, no defaults, reset to empty configuration
        this.config = new YamlConfiguration();
        return this.config;
    }

    public FileConfiguration getConfig() {
        if (this.config == null) this.load();
        return this.config;
    }

    /**
     * Save the configuration file immediately. All cached save requests will be
     * saved to the file system
     */
    public void save() {
        this.save(true);
    }

    /**
     * Request a save of the configuration file. If request is not required to
     * be done immediately and last save was less than configured max frequency
     * then request will be cached and a scheduled task will kick off after the
     * max frequency has expired since last save.
     *
     * @param immediately true to force a save of the configuration file immediately
     */
    public void save(final boolean immediately) {
        if (!immediately) {
            // Determine how long since last save attempt
            long sinceLastSave = this.maxSaveFrequency;
            if (this.lastSaveAttempt != null)
                sinceLastSave = (System.currentTimeMillis() - this.lastSaveAttempt) / 1000;

            // Schedule a cache flush to run if last save was less than maximum save frequency
            if (sinceLastSave < this.maxSaveFrequency) {
                // If task already scheduled let it run when expected
                if (this.isSaveQueued()) {
                    this.owner.getLogger().log(Level.FINEST, "Save request already queued for file: " + this.file + " (Last save was " + sinceLastSave + " seconds ago)");
                    return;
                }

                this.owner.getLogger().log(Level.FINEST, "Queueing save request to run in " + (this.maxSaveFrequency - sinceLastSave) + " seconds for file: " + this.file + " (Last save was " + sinceLastSave + " seconds ago)");

                // Schedule task to save cache to file system
                final ConfigurationFile that = this;
                this.taskSave = this.owner.getServer().getScheduler().scheduleSyncDelayedTask(
                          this.owner
                        , new Runnable() { @Override public void run() { that.save(true); } }
                        , (this.maxSaveFrequency - sinceLastSave) * ConfigurationFile.TICKS_PER_SECOND
                );

                return;
            }
        }

        try {
            this.config.save(this.file);

        } catch (final IOException e) {
            this.owner.getLogger().log(Level.SEVERE, "Unable to save configuration file: " + this.file, e);
            return;

        } finally {
            this.lastSaveAttempt = System.currentTimeMillis();
        }

        this.taskSave = null;

        this.owner.getLogger().log(Level.FINEST, "Saved configuration file: " + this.file);
    }

    /**
     * Determine if save request is currently scheduled to execute.
     *
     * @return true if save request is pending; otherwise false
     */
    public boolean isSaveQueued() {
        return (this.taskSave != null && this.owner.getServer().getScheduler().isQueued(this.taskSave));
    }

    public FileVersion getVersion() {
        return new FileVersion(this.config.getString("version"));
    }

    public final class FileVersion implements Comparable<FileVersion> {

        public final String original;
        public final Integer major;
        public final Integer minor;
        public final Integer revision;
        public final String type;
        public final Integer build;

        FileVersion(final String version) {
            this.original = version;
            if (this.original == null) {
                this.major = null;
                this.minor = null;
                this.revision = null;
                this.type = null;
                this.build = null;
                return;
            }

            final String[] split = Pattern.compile("\\.|a|b|rc").split(this.original);
            this.major = Integer.parseInt(split[0]);
            this.minor = Integer.parseInt(split[1]);
            this.revision = Integer.parseInt(split[2]);
            final Matcher m = Pattern.compile("a|b|rc").matcher(this.original);
            this.type = (m.find() ? m.group(0) : null);
            this.build = (split.length >= 4 ? Integer.parseInt(split[3]) : null);
        }

        @Override
        public String toString() {
            return this.original;
        }

        @Override
        public int compareTo(final FileVersion other) {
            // Null instances are less than any non-null instances
            if (other == null && this == null) return 0;
            if (other == null) return 1;
            if (this == null) return -1;

            // Null versions are less than any non-null version
            if (other.toString() == null && this.toString() == null) return 0;
            if (other.toString() == null) return 1;
            if (this.toString() == null) return -1;

            // Identical character matches are equivalent
            if (this.toString().equals(other.toString())) return 0;

            // Determine what is different, favoring the more important segments first
            if (this.major != other.major) return Integer.valueOf(this.major).compareTo(other.major);
            if (this.minor != other.minor) return Integer.valueOf(this.minor).compareTo(other.minor);
            if (this.revision != other.revision) return Integer.valueOf(this.revision).compareTo(other.revision);

            // A full release is more than a pre-release
            if (this.type == null) return 1;
            if (other.type == null) return -1;

            // Check the pre-release segments
            if (!this.type.equals(other.type)) return this.type.compareTo(other.type);
            return this.build.compareTo(other.build);
        }

    }

}
