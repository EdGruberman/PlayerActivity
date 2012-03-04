package edgruberman.bukkit.playeractivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;

/**
 * Standardized plugin configuration file management class.<br />
 * <br />
 * Defaults will be extracted from the JAR.  Save requests can be queued to
 * avoid performance penalties for too many save requests occurring too
 * frequently.
 */
public final class ConfigurationFile {

    /**
     * Standard plugin configuration file.
     */
    private static final String PLUGIN_FILE = "config.yml";

    /**
     * Path in JAR to find files containing default values.
     */
    private static final String DEFAULTS = "/defaults/";

    /**
     * Default maximum save frequency.
     */
    private static final int DEFAULT_SAVE = -1;

    /**
     * Clock ticks per second; Used to determine durations between saves.
     */
    private static final int TICKS_PER_SECOND = 20;

    private final Plugin owner;
    private final File file;
    private final String defaults;
    private FileConfiguration config;
    private int maxSaveFrequency;
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
        this(owner, null);
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
        this(owner, null, null, maxSaveFrequency);
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
        this(owner, file, null);
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
        this(owner, file, defaults, ConfigurationFile.DEFAULT_SAVE);
    }

    /**
     * Construct configuration file reference for standardized load and save
     * management.
     *
     * @param owner plugin that owns this configuration file
     * @param file name of file in the default data directory
     * @param defaults path to default configuration file supplied in JAR
     * @param maxSaveFrequency shortest duration in seconds each save can occur
     */
    ConfigurationFile(final Plugin owner, final String file, final String defaults, final int maxSaveFrequency) {
        this.owner = owner;

        this.file = new File(this.owner.getDataFolder(), (file != null ? file : ConfigurationFile.PLUGIN_FILE));
        this.defaults = (defaults != null ? defaults : ConfigurationFile.DEFAULTS + this.file.getName());
        this.maxSaveFrequency = maxSaveFrequency;
    }

    /**
     * Loads the configuration file from owning plugin's data folder.  If file
     * exists, it will be expected to be properly configured.  If file does not
     * exist and defaults are supplied in the JAR, the defaults will be used.
     * Otherwise an empty configuration will be set.
     */
    FileConfiguration load() {
        // Flush any pending save requests first to avoid losing any previous edits not yet committed
        if (this.isSaveQueued()) this.save();

        this.config = YamlConfiguration.loadConfiguration(this.file);
        if (this.file.exists()) return this.config;

        // Check if defaults are supplied in JAR
        final InputStream defaults = (this.defaults != null ? this.owner.getClass().getResourceAsStream(this.defaults) : null);
        if (defaults == null) {
            // No file, no defaults, reset to empty configuration
            this.config = new YamlConfiguration();
            return this.config;
        }

        // Load defaults supplied in JAR
        this.config.setDefaults(YamlConfiguration.loadConfiguration(defaults));
        this.config.options().copyDefaults(true);
        this.save();

        this.config = YamlConfiguration.loadConfiguration(this.file);
        return this.config;
    }

    int getMaxSaveFrequency() {
        return this.maxSaveFrequency;
    }

    void setMaxSaveFrequency(final int frequency) {
        this.maxSaveFrequency = frequency;
    }

    FileConfiguration getConfig() {
        return this.config;
    }

    /**
     * Save the configuration file immediately. All cached save requests will be
     * saved to the file system
     */
    void save() {
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
    void save(final boolean immediately) {
        if (!immediately) {
            // Determine how long since last save attempt.
            long sinceLastSave = this.maxSaveFrequency;
            if (this.lastSaveAttempt != null)
                sinceLastSave = (System.currentTimeMillis() - this.lastSaveAttempt) / 1000;

            // Schedule a cache flush to run if last save was less than maximum save frequency.
            if (sinceLastSave < this.maxSaveFrequency) {
                // If task already scheduled let it run when expected.
                if (this.isSaveQueued()) {
                    Main.messageManager.log("Save request already queued; Last save was " + sinceLastSave + " seconds ago; " + this.file, MessageLevel.FINEST);
                    return;
                }

                Main.messageManager.log("Queueing configuration file save request to run in " + (this.maxSaveFrequency - sinceLastSave) + " seconds; Last save was " + sinceLastSave + " seconds ago; " + this.file, MessageLevel.FINEST);

                // Schedule task to save cache to file system.
                final ConfigurationFile that = this;
                this.taskSave = this.owner.getServer().getScheduler().scheduleSyncDelayedTask(
                          this.owner
                        , new Runnable() { @Override
                        public void run() { that.save(true); } }
                        , (this.maxSaveFrequency - sinceLastSave) * ConfigurationFile.TICKS_PER_SECOND
                );

                return;
            }
        }

        try {
            this.config.save(this.file);

        } catch (final IOException e) {
            Main.messageManager.log("Unable to save configuration file; " + this.file, MessageLevel.SEVERE, e);
            return;

        } finally {
            this.lastSaveAttempt = System.currentTimeMillis();
        }

        this.taskSave = null;

        Main.messageManager.log("Saved configuration file; " + this.file, MessageLevel.FINEST);
    }

    /**
     * Determine if save request is currently scheduled to execute.
     *
     * @return true if save request is pending; otherwise false
     */
    boolean isSaveQueued() {
        return (this.taskSave != null && this.owner.getServer().getScheduler().isQueued(this.taskSave));
    }

}
