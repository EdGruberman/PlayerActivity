package edgruberman.bukkit.playeractivity.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

/**
 * handles message delivery and logging;
 * uses message patterns stored in a {@link org.bukkit.configuration.ConfigurationSection ConfigurationSection}
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 2.0.0
 */
public class ConfigurationCourier extends Courier {

    /** section containing message patterns; null if basePath should be used */
    protected final ConfigurationSection base;

    /** prepends a timestamp to all messages and uses plugin root configuration for message patterns */
    public ConfigurationCourier(final Plugin plugin) {
        super(plugin);
        this.base = this.plugin.getConfig();
    }

    protected ConfigurationCourier(final ConfigurationCourier.Factory parameters) {
        super(parameters);
        this.base = parameters.base;
    }

    /** @return section all message pattern paths are referenced from */
    public ConfigurationSection getBase() {
        return this.base;
    }

    @Override
    public List<Message> draft(final String key, final Object... arguments) {
        if (this.base.isString(key))
            return super.draft(this.base.getString(key), arguments);

        if (!this.base.isList(key)) return Collections.emptyList();

        final List<Message> messages = new ArrayList<Message>();
        for (final String item : this.base.getStringList(key)) {
            if (item == null) continue;

            final Message.Factory factory = Message.Factory.create(item, arguments);
            if (this.timestamp) factory.timestamp();
            messages.add(factory.build());
        }
        return messages;
    }

    /**
     * retrieve a message pattern from the configuration and format with supplied arguments
     *
     * @param key path to message pattern stored in configuration base
     */
    @Override
    public String format(final String key, final Object... arguments) {
        return super.format(this.getBase().getString(key), arguments);
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

        /** @param path path to section in plugin configuration containing message patterns */
        public Factory setBase(final String path) {
            this.base = this.plugin.getConfig().getConfigurationSection(path);
            return this;
        }

        /** @param section base section containing message patterns */
        public Factory setBase(final ConfigurationSection section) {
            this.base = section;
            return this;
        }

        @Override
        public ConfigurationCourier build() {
            return new ConfigurationCourier(this);
        }

    }

}
