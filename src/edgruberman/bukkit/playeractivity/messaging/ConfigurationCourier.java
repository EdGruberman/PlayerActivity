package edgruberman.bukkit.playeractivity.messaging;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

/**
 * handles message delivery and logging;
 * uses message patterns stored in a {@link org.bukkit.configuration.ConfigurationSection ConfigurationSection}
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 5.0.0
 */
public class ConfigurationCourier extends Courier {

    /** section containing message patterns */
    protected final ConfigurationSection base;

    protected ConfigurationCourier(final ConfigurationCourier.Factory parameters) {
        super(parameters);
        this.base = parameters.base;
    }

    /** @return section all message pattern paths are referenced from */
    public ConfigurationSection getBase() {
        return this.base;
    }

    /**
     * preliminary Message construction before formatting for target recipient (timestamp argument prepended if configured)
     *
     * @param key path to message text that can contain format elements in base configuration
     */
    public Message compose(final String key, final Object... arguments) {
        return this.draft(this.base.getString(key), arguments);
    }

    /**
     * retrieve a message pattern from the configuration and format with supplied arguments
     *
     * @param key relative path from base to pattern
     */
    @Override
    public String format(final String key, final Object... arguments) {
        return super.format(this.getBase().getString(key), arguments);
    }

    public void send(final CommandSender target, final String key, final Object... arguments) {
        this.sendMessage(target, this.base.getString(key), arguments);
    }

    public void broadcast(final String key, final Object... arguments) {
        this.broadcastMessage(this.base.getString(key), arguments);
    }

    public void world(final World target, final String key, final Object... arguments) {
        this.worldMessage(target, this.base.getString(key), arguments);
    }

    public void publish(final String permission, final String key, final Object... arguments) {
        this.publishMessage(permission, this.base.getString(key), arguments);
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
        public Factory setColorCode(final String key) {
            final String value = this.base.getString(key);
            if (value == null) throw new IllegalArgumentException("Color code not found: " + this.base.getCurrentPath() + this.base.getRoot().options().pathSeparator() + key);
            this.setColorCode(value.charAt(0));
            return this;
        }

        @Override
        public Factory setTimestamp(final boolean timestamp) {
            super.setTimestamp(timestamp);
            return this;
        }

        @Override
        public Factory setColorCode(final char colorCode) {
            super.setColorCode(colorCode);
            return this;
        }

        @Override
        public ConfigurationCourier build() {
            return new ConfigurationCourier(this);
        }

    }

}
