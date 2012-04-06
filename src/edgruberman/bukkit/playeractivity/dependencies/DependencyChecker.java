package edgruberman.bukkit.playeractivity.dependencies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

public final class DependencyChecker {

    private static final List<PluginDependency> MINIMUMS = Arrays.<PluginDependency>asList(
            new PluginDependency("MessageManager", "edgruberman.bukkit.messagemanager.Main", "6.1.0")
    );

    private final Plugin plugin;

    public DependencyChecker(final Plugin plugin) {
        this.plugin = plugin;
        this.checkDependencies();
    }

    /**
     * Install or update any plugins this plugin is dependent upon.
     */
    private void checkDependencies() {
        boolean isRestartRequired = false;

        for (final PluginDependency minimum : DependencyChecker.MINIMUMS) {
            final Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin(minimum.name);

            // Missing
            if (plugin == null) {
                this.enablePlugin(minimum);
                continue;
            }

            // Conflict
            if (!plugin.getDescription().getMain().equals(minimum.main))
                throw new IllegalStateException("Dependency Failure; Plugin conflict: " + plugin.getName() + "; " + plugin.getDescription().getMain() + " does not match required " + minimum.main);

            final Version existing = new Version(plugin.getDescription().getVersion());
            if (existing.compareTo(minimum.version) >= 0) continue;

            // Old
            this.installPlugin(minimum.name, this.plugin.getServer().getUpdateFolderFile());
            this.plugin.getLogger().log(Level.SEVERE, "Dependency requires update for " + minimum.name + " v" + minimum.version.original + "; Restart your server as soon as possible to automatically apply the update");
            isRestartRequired = true;
        }

        if (isRestartRequired) throw new IllegalStateException("Dependency Failure; Server restart required");
    }

    private void enablePlugin(final PluginDependency dependency) {
        // Install
        final File pluginJar = this.installPlugin(dependency.name, this.plugin.getDataFolder().getParentFile());

        // Load
        final Plugin plugin;
        try {
            plugin = this.plugin.getServer().getPluginManager().loadPlugin(pluginJar);
            plugin.onLoad();
        } catch (final Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Error loading plugin: " + dependency.name, t);
            throw new IllegalStateException("Dependency Failure; Unable to load plugin: " + dependency.name + " from \"" + pluginJar.getPath() + "\"");
        }
        this.plugin.getLogger().log(Level.INFO, "Loaded plugin: " + dependency.name + " v" + dependency.version.original);

        // Enable
        try {
            this.plugin.getServer().getPluginManager().enablePlugin(plugin);
        } catch (final Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Error enabling plugin: " + dependency.name, t);
            throw new IllegalStateException("Dependency Failure; Unable to enable plugin: " + dependency.name);
        }
        this.plugin.getLogger().log(Level.INFO, "Enabled plugin: " + dependency.name + " v" + dependency.version.original);
    }

    /**
     * Extract embedded plugin file from JAR.
     *
     * @param name plugin name
     * @param outputFolder where to play plugin file
     * @return plugin file on the file system
     */
    private File installPlugin(final String name, final File outputFolder) {
        final URL source = this.getClass().getResource("/lib/" + name + ".jar");
        final File pluginJar = new File(outputFolder, name + ".jar");
        this.extract(source, pluginJar);
        this.plugin.getLogger().log(Level.INFO, "Installed plugin: " + name + " to \"" + pluginJar.getPath() + "\"");
        return pluginJar;
    }

    /**
     * Save a file to the local file system.
     */
    private void extract(final URL source, final File destination) {
        destination.getParentFile().mkdir();

        InputStream in = null;
        OutputStream out = null;
        int len;
        final byte[] buf = new byte[4096];

        try {
            in = source.openStream();
            out = new FileOutputStream(destination);
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);

        } catch (final Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to extract \"" + source.getFile() + "\" to \"" + destination.getPath() + "\"", e);

        } finally {
            try { if (in != null) in.close(); } catch (final Exception e) { e.printStackTrace(); }
            try { if (out != null) out.close(); } catch (final Exception e) { e.printStackTrace(); }
        }
    }

}
