package edgruberman.bukkit.playeractivity.messaging;

import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

/**
 * handles message delivery and logging; uses keys to reference message patterns stored in a {@link org.bukkit.configuration.ConfigurationSection ConfigurationSection}
 * @author EdGruberman (ed@rjump.com)
 * @version 5.1.2
 */
public class ConfigurationCourier extends Courier {

    /** message pattern container */
    protected final ConfigurationSection base;

    protected ConfigurationCourier(final ConfigurationCourier.Factory parameters) {
        super(parameters);
        this.base = parameters.base;
    }

    /** @return section all message pattern key paths are relative to */
    public ConfigurationSection getBase() {
        return this.base;
    }

    /**
     * @param key path relative to {@link #getBase base} that contains message pattern
     * @return String value in configuration; null if not a String
     */
    public String pattern(final String key) {
        if (!this.base.isString(key)) {
            this.plugin.getLogger().log(Level.FINEST, "String value not found for {0} in {1}", new Object[] { key, ( this.base.getCurrentPath().equals("") ? "(root)" : this.base.getCurrentPath() ) });
            return null;
        }
        return this.base.getString(key);
    }

    /**
     * preliminary Message construction before formatting for target recipient (timestamp argument prepended if configured)
     * @param key path relative to {@link #getBase base} that contains message pattern
     */
    public Message compose(final String key, final Object... arguments) {
        final String pattern = this.pattern(key);
        if (pattern == null) return null;
        return this.draft(pattern, arguments);
    }

    /**
     * retrieve a message pattern from the configuration and format with supplied arguments
     * @param key path relative to {@link #getBase base} that contains message pattern
     */
    @Override
    public String format(final String key, final Object... arguments) {
        final String pattern = this.pattern(key);
        if (pattern == null) return null;
        return super.format(pattern, arguments);
    }

    /**
     * deliver message to individual player
     * @param key path relative to {@link #getBase base} that contains message pattern (null and missing patterns are silently ignored and not sent)
     */
    public void send(final CommandSender target, final String key, final Object... arguments) {
        final String pattern = this.pattern(key);
        if (pattern == null) return;
        this.sendMessage(target, pattern, arguments);
    }

    /**
     * deliver message to all players on server
     * @param key path relative to {@link #getBase base} that contains message pattern (null and missing patterns are silently ignored and not sent)
     */
    public void broadcast(final String key, final Object... arguments) {
        final String pattern = this.pattern(key);
        if (pattern == null) return;
        this.broadcastMessage(pattern, arguments);
    }

    /**
     * deliver message to players in a world
     * @param key path relative to {@link #getBase base} that contains message pattern (null and missing patterns are silently ignored and not sent)
     */
    public void world(final World target, final String key, final Object... arguments) {
        final String pattern = this.pattern(key);
        if (pattern == null) return;
        this.worldMessage(target, pattern, arguments);
    }

    /**
     * deliver message to players with a permission
     * @param key path relative to {@link #getBase base} that contains message pattern (null and missing patterns are silently ignored and not sent)
     */
    public void publish(final String permission, final String key, final Object... arguments) {
        final String pattern = this.pattern(key);
        if (pattern == null) return;
        this.publishMessage(permission, pattern, arguments);
    }



    public static Factory create(final Plugin plugin) {
        return Factory.create(plugin);
    }

    public static class Factory extends Courier.Factory {

        /** prepends a timestamp to all messages and retrieves message patterns from plugin root configuration */
        public static Factory create(final Plugin plugin) {
            return new Factory(plugin);
        }

        public ConfigurationSection base;

        protected Factory(final Plugin plugin) {
            super(plugin);
            this.setBase(plugin.getConfig());
        }

        /** @param section base section containing message patterns */
        public Factory setBase(final ConfigurationSection section) {
            if (section == null) throw new IllegalArgumentException("ConfigurationSection can not be null");
            this.base = section;
            return this;
        }

        /** @param path path to section relative to current base section containing message patterns */
        public Factory setPath(final String path) {
            final ConfigurationSection section = this.base.getConfigurationSection(path);
            if (section == null) throw new IllegalArgumentException("ConfigurationSection not found: " + path);
            this.setBase(section);
            return this;
        }

        /** @param key path to color code prefix character in base configuration */
        public Factory setFormatCode(final String key) {
            final String value = this.base.getString(key);
            if (value == null) throw new IllegalArgumentException("Color code not found: " + this.base.getCurrentPath() + this.base.getRoot().options().pathSeparator() + key);
            this.setFormatCode(value.charAt(0));
            return this;
        }

        @Override
        public Factory setTimestamp(final boolean timestamp) {
            super.setTimestamp(timestamp);
            return this;
        }

        @Override
        public Factory setFormatCode(final char colorCode) {
            super.setFormatCode(colorCode);
            return this;
        }

        @Override
        public ConfigurationCourier build() {
            return new ConfigurationCourier(this);
        }

    }

}
