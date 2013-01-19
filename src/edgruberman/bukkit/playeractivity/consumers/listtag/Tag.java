package edgruberman.bukkit.playeractivity.consumers.listtag;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.Main;

public abstract class Tag implements Comparable<Tag> {

    private static final int LIST_NAME_LENGTH = 16;
    private static final String TAGS_PACKAGE = Tag.class.getPackage().getName() + ".tags";

    public static Tag create(final String className, final ConfigurationSection config, final ListTag listTag, final Plugin plugin)
            throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException
                , InvocationTargetException, NoSuchMethodException, ClassCastException, ClassNotFoundException {
        return Tag
                .find(className)
                .getConstructor(ConfigurationSection.class, ListTag.class, Plugin.class)
                .newInstance(config, listTag, plugin);
    }

    public static Class<? extends Tag> find(final String className) throws ClassNotFoundException, ClassCastException {
        try {
            return Class.forName(Tag.TAGS_PACKAGE + "." + className).asSubclass(Tag.class);
        } catch (final Exception e) {
            return Class.forName(className).asSubclass(Tag.class);
        }
    }



    // ---- instance ----

    protected final String pattern;
    protected final int length;
    protected final String description;
    protected final Integer priority;
    protected final ListTag listTag;
    protected final Plugin plugin;
    protected final Map<String, Long> attached = new HashMap<String, Long>();

    public Tag(final ConfigurationSection config, final ListTag listTag, final Plugin plugin) {
        this.pattern = config.getString("pattern");
        this.length = MessageFormat.format(this.pattern, "").length();
        this.description = config.getString("description");
        this.priority = config.getInt("priority");
        this.listTag = listTag;
        this.plugin = plugin;
    }

    public void attach(final Player player) {
        this.attach(player, System.currentTimeMillis());
    }

    public void attach(final Player player, final long attached) {
        this.attached.put(player.getName(), attached);
        this.listTag.attach(this, player);
    }

    public void detach(final Player player) {
        this.attached.remove(player.getName());
        this.listTag.detach(this, player);
    }

    public final void unload() {
        // TODO CME?
        this.onUnload();
        this.attached.clear();
    }

    protected abstract void onUnload();

    @Override
    public int compareTo(final Tag o) {
        return ( o == null ? -1 : this.priority.compareTo(o.priority) );
    }

    public String getPlayerListName(final Player player) {
        final String name = player.getName().substring(0, Math.min(player.getName().length(), Tag.LIST_NAME_LENGTH - this.length));
        return MessageFormat.format(this.pattern, name);
    }

    public String getDisplayName(final Player player) {
        return MessageFormat.format(this.pattern, player.getDisplayName());
    }

    public final String describe(final Player player) {
        final List<Object> arguments = new ArrayList<Object>();
        if (!this.attached.containsKey(player.getName())) return null;
        final Long attached = System.currentTimeMillis() - this.attached.get(player.getName());
        final String duration = ( attached != null ? Main.readableDuration(attached) : null );
        arguments.add(duration);
        arguments.add(( attached != null ? 1 : 0 ));
        arguments.add(this.getPlayerListName(player));
        return this.onDescribe(player, arguments);
    }

    protected String onDescribe(final Player player, final List<Object> arguments) {
        return MessageFormat.format(this.description, arguments.toArray());
    }

}
