package edgruberman.bukkit.playeractivity.messaging;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

/**
 * handles message delivery and logging
 * @author EdGruberman (ed@rjump.com)
 * @version 6.0.1
 */
public class Courier {

    protected final Plugin plugin;
    protected final boolean timestamp;

    protected Courier(final Courier.Factory parameters) {
        this.plugin = parameters.plugin;
        this.timestamp = parameters.timestamp;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * @return true if all messages will have their arguments automatically
     * prepended with the current date/time
     */
    public boolean getTimestamp() {
        return this.timestamp;
    }

    /** format a pattern with supplied arguments */
    public String formatMessage(final String pattern, final Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }

    /**
     * preliminary Message construction before formatting for target
     * recipient (timestamp argument prepended if configured)
     * @param pattern message text that contains format elements
     */
    public Message draft(final String pattern, final Object... arguments) {
        final Message.Factory factory = Message.create(pattern, arguments);
        if (this.timestamp) factory.timestamp();
        return factory.build();
    }

    /**
     * deliver message to recipients and record log entry
     * (this will not timestamp the message)
     */
    public void submit(final Recipients recipients, final Message message) {
        try {
            final Confirmation confirmation = message.deliver(recipients);
            this.plugin.getLogger().log(confirmation.toLogRecord());

        } catch (final RuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "Error submitting message for delivery; pattern: \"{0}\"{1}; {2}", new Object[] { message.original, ChatColor.RESET, e });
        }
    }

    /** deliver message to individual player */
    public void sendMessage(final CommandSender sender, final String pattern, final Object... arguments) {
        final Recipients recipients = Recipients.Sender.create(sender);
        final Message message = this.draft(pattern, arguments);
        this.submit(recipients, message);
    }

    /** deliver message to all players on server */
    public void broadcastMessage(final String pattern, final Object... arguments) {
        final Recipients recipients = new Recipients.ServerPlayers();
        final Message message = this.draft(pattern, arguments);
        this.submit(recipients, message);
    }

    /** deliver message to players in a world */
    public void worldMessage(final World world, final String pattern, final Object... arguments) {
        final Recipients recipients = new Recipients.WorldPlayers(world);
        final Message message = this.draft(pattern, arguments);
        this.submit(recipients, message);
    }

    /** deliver message to players with a permission */
    public void publishMessage(final String permission, final String pattern, final Object... arguments) {
        final Recipients recipients = new Recipients.PermissionSubscribers(permission);
        final Message message = this.draft(pattern, arguments);
        this.submit(recipients, message);
    }



    public static Factory create(final Plugin plugin) {
        return Factory.create(plugin);
    }

    public static class Factory {

        /** prepends a timestamp to all messages */
        public static Factory create(final Plugin plugin) {
            return new Factory(plugin);
        }

        protected final Plugin plugin;
        protected boolean timestamp;

        protected Factory(final Plugin plugin) {
            this.plugin = plugin;
            this.setTimestamp(true);
        }

        /**
         * @param timestamp true to prepend timestamp to arguments
         * of all messages
         */
        public Factory setTimestamp(final boolean timestamp) {
            this.timestamp = true;
            return this;
        }

        public Courier build() {
            return new Courier(this);
        }

    }



    /**
     * handles message delivery and logging; uses keys to reference message
     * patterns stored in a
     * {@link org.bukkit.configuration.ConfigurationSection
     * ConfigurationSection}
     * @version 7.0.0
     */
    public static class ConfigurationCourier extends Courier {

        protected static final char DEFAULT_FORMAT_CODE = ChatColor.COLOR_CHAR;

        /** message pattern container */
        protected final ConfigurationSection base;
        protected final char formatCode;

        protected ConfigurationCourier(final ConfigurationCourier.Factory parameters) {
            super(parameters);
            this.base = parameters.base;
            this.formatCode = parameters.formatCode;
        }

        /** @return section all message pattern key paths are relative to */
        public ConfigurationSection getBase() {
            return this.base;
        }

        /** @return section at path relative to {@link #getBase base} */
        public ConfigurationSection getSection(final String path) {
            return this.base.getConfigurationSection(path);
        }

        /** @return prefix that designates format code in message patterns */
        public char getFormatCode() {
            return this.formatCode;
        }

        /**
         * @param key path relative to {@link #getBase base} that contains
         * message pattern
         * @return pattern at key translated into Minecraft formatting codes;
         * empty list if key contains null or an empty String
         */
        public List<String> translate(final String key) {
            final List<String> result = this.getStringList(key);
            if (result.size() == 0 || result.get(0) == null || result.get(0).equals("")) {
                this.plugin.getLogger().log(Level.FINEST, "String value not found for {0} in {1}", new Object[] { key, ( this.base.getCurrentPath().equals("") ? "(root)" : this.base.getCurrentPath() ) });
                return Collections.emptyList();
            }

            if (this.formatCode != ChatColor.COLOR_CHAR) {
                for (int i = 0; i < result.size(); i++) {
                    final String translated = ChatColor.translateAlternateColorCodes(this.formatCode, result.get(i));
                    result.set(i, translated);
                }
            }

            return result;
        }

        /**
         * preliminary Message construction before formatting for target
         * recipient (timestamp argument prepended if configured)
         * @param key path relative to {@link #getBase base} that contains
         * message pattern
         * @return composed Message, null if key is null or empty string
         */
        public Message compose(final String key, final Object... arguments) {
            final List<String> translated = this.translate(key);

            Message result = null;
            for (final String pattern : translated) {
                final Message drafted = this.draft(pattern, arguments);
                if (result == null) {
                    result = drafted;
                } else {
                    result.append(drafted);
                }
            }

            return result;
        }

        /**
         * retrieve a message pattern from the configuration and format with
         * supplied arguments
         * @param key path relative to {@link #getBase base} that contains
         * message pattern
         */
        public List<String> format(final String key, final Object... arguments) {
            final List<String> result = this.translate(key);
            for (int i = 0; i < result.size(); i++) {
                final String formatted = this.formatMessage(result.get(i), arguments);
                result.set(i, formatted);
            }
            return result;
        }

        /**
         * deliver message to individual player
         * @param key path relative to {@link #getBase base} that contains
         * message pattern (null and empty strings patterns are silently
         * ignored and not sent)
         */
        public void send(final CommandSender sender, final String key, final Object... arguments) {
            final List<String> translated = this.translate(key);
            if (translated.size() == 0) return;
            for (final String pattern : translated) this.sendMessage(sender, pattern, arguments);
        }

        /**
         * deliver message to all players on server
         * @param key path relative to {@link #getBase base} that contains
         * message pattern (null and missing patterns are silently ignored
         * and not sent)
         */
        public void broadcast(final String key, final Object... arguments) {
            final List<String> translated = this.translate(key);
            if (translated.size() == 0) return;
            for (final String pattern : translated) this.broadcastMessage(pattern, arguments);
        }

        /**
         * deliver message to players in a world
         * @param key path relative to {@link #getBase base} that contains
         * message pattern (null and missing patterns are silently ignored
         * and not sent)
         */
        public void world(final World world, final String key, final Object... arguments) {
            final List<String> translated = this.translate(key);
            if (translated.size() == 0) return;
            for (final String pattern : translated) this.worldMessage(world, pattern, arguments);
        }

        /**
         * deliver message to players with a permission
         * @param key path relative to {@link #getBase base} that contains
         * message pattern (null and missing patterns are silently ignored
         * and not sent)
         */
        public void publish(final String permission, final String key, final Object... arguments) {
            final List<String> translated = this.translate(key);
            if (translated.size() == 0) return;
            for (final String pattern : translated) this.publishMessage(permission, pattern, arguments);
        }

        protected List<String> getStringList(final String key) {
            if (this.base.isList(key))
                return this.base.getStringList(key);

            if (this.base.isString(key))
                return Arrays.asList(this.base.getString(key));

            return Collections.emptyList();
        }



        public static Factory create(final Plugin plugin) {
            return Factory.create(plugin);
        }

        public static class Factory extends Courier.Factory {

            /**
             * prepends a timestamp to all messages and retrieves message
             * patterns from plugin root configuration
             */
            public static Factory create(final Plugin plugin) {
                return new Factory(plugin);
            }

            protected ConfigurationSection base;
            protected char formatCode;

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

            /**
             * @param key path to format code prefix character in base
             * configuration
             */
            public Factory setFormatCode(final String key) {
                final String value = this.base.getString(key);
                if (value == null) throw new IllegalArgumentException("Color code not found: " + this.base.getCurrentPath() + this.base.getRoot().options().pathSeparator() + key);
                this.setFormatCode(value.charAt(0));
                return this;
            }

            /**
             * @param formatCode prefix that designates a format code
             * in message patterns (default is
             * {@value org.bukkit.ChatColor#COLOR_CHAR}, common alternate
             * is &) */
            public Factory setFormatCode(final char formatCode) {
                this.formatCode = formatCode;
                return this;
            }

            @Override
            public Factory setTimestamp(final boolean timestamp) {
                super.setTimestamp(timestamp);
                return this;
            }

            @Override
            public ConfigurationCourier build() {
                return new ConfigurationCourier(this);
            }

        }

    }

}
