package edgruberman.bukkit.playeractivity.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

/**
 * handles message delivery and logging;
 * uses message patterns stored in a {@link org.bukkit.configuration.ConfigurationSection ConfigurationSection}
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 4.1.0
 */
public class ConfigurationCourier extends Courier {

    /** section containing message patterns; null if basePath should be used */
    protected final ConfigurationSection base;

    protected ConfigurationCourier(final ConfigurationCourier.Factory parameters) {
        super(parameters);
        this.base = parameters.base;
    }

    /** @return section all message pattern paths are referenced from */
    public ConfigurationSection getBase() {
        return this.base;
    }

    /** draft Messages (single for string value, multiple for string list value) */
    public List<Message> compose(final String key, final Object... arguments) {
        final List<Message> messages = new ArrayList<Message>();

        if (this.base.isString(key)) {
            messages.add(this.draft(this.base.getString(key), arguments));
            return messages;
        }

        if (!this.base.isList(key)) {
            this.plugin.getLogger().log(Level.FINEST, "Unusable Message pattern \"{1}\" at \"{0}\" key", new Object[] { this.base.get(key), key });
            return Collections.emptyList();
        }

        for (final String pattern : this.base.getStringList(key)) messages.add(this.draft(pattern, arguments));
        return messages;
    }

    /** deliver messages to recipients and record log entry for each message (this will not timestamp the message) */
    public void submit(final Recipients recipients, final List<Message> messages) {
        for (final Message message : messages) this.submit(recipients, message);
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

    @Override
    public void send(final CommandSender target, final String key, final Object... arguments) {
        final Recipients recipients = new Individual(target);
        final List<Message> messages = this.compose(key, arguments);
        this.submit(recipients, messages);
    }

    @Override
    public void broadcast(final String key, final Object... arguments) {
        final Recipients recipients = new ServerPlayers();
        final List<Message> messages = this.compose(key, arguments);
        this.submit(recipients, messages);
    }

    @Override
    public void world(final World target, final String key, final Object... arguments) {
        final Recipients recipients = new WorldPlayers(target);
        final List<Message> messages = this.compose(key, arguments);
        this.submit(recipients, messages);
    }

    @Override
    public void publish(final String permission, final String key, final Object... arguments) {
        final Recipients recipients = new PermissionSubscribers(permission);
        final List<Message> messages = this.compose(key, arguments);
        this.submit(recipients, messages);
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
            this.base = section;
            return this;
        }

        /** @param path path to section relative to current base section containing message patterns */
        public Factory setPath(final String path) {
            this.base = this.base.getConfigurationSection(path);
            return this;
        }

        /** @param key path to color code prefix character in base configuration */
        public Factory setColorCode(final String key) {
            this.setColorCode(this.base.getString(key).charAt(0));
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
