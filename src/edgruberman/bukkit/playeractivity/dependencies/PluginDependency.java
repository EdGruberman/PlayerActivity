package edgruberman.bukkit.playeractivity.dependencies;

public class PluginDependency {

    public final String name;
    public final String main;
    public final Version version;

    PluginDependency(final String name, final String main, final String version) {
        this.name = name;
        this.main = main;
        this.version = new Version(version);
    }

}
