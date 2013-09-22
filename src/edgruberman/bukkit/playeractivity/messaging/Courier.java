package edgruberman.bukkit.playeractivity.messaging;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

/**
 * handles message delivery and logging
 * @author EdGruberman (ed@rjump.com)
 * @version 8.0.0
 */
public interface Courier {

    /** format a pattern with supplied arguments */
    public String format(final String message, final Object... arguments);

    /**
     * preliminary Message construction before formatting for target recipient
     * @param pattern message text that contains format elements
     */
    Message draft(final String message, final Object... arguments);

    /** deliver message to recipients and record log entry */
    void submit(final RecipientList recipients, final Message message);

    /** deliver message to individual player */
    void send(final CommandSender sender, final String message, final Object... arguments);

    /** deliver message to players on server */
    void broadcast(final String message, final Object... arguments);

    /** deliver message to players in a world */
    void announce(final World world, final String message, final Object... arguments);

    /** deliver message to players that have a permission */
    void publish(final String permission, final String message, final Object... arguments);





    /**
     * message content is defined by a MessageFormat pattern
     * @version 8.0.0
     */
    public class PatternCourier implements Courier {

        public static final boolean DEFAULT_TIMESTAMP = true;

        protected final Logger logger;
        protected final Server server;
        protected final boolean timestamp;

        protected PatternCourier(final PatternCourier.Factory parameters) {
            this.logger = parameters.logger;
            this.server = parameters.server;
            this.timestamp = parameters.timestamp;
        }

        public Logger getLogger() {
            return this.logger;
        }

        /**
         * @return true if all messages will have their arguments automatically
         * prepended with the current date/time
         */
        public boolean getTimestamp() {
            return this.timestamp;
        }

        /** format a pattern with supplied arguments */
        @Override
        public String format(final String pattern, final Object... arguments) {
            return MessageFormat.format(pattern, arguments);
        }

        /** {@inheritDoc} (timestamp argument prepended if configured) */
        @Override
        public Message draft(final String pattern, final Object... arguments) {
            final Message.Factory factory = Message.Factory.create(pattern, arguments);
            if (this.timestamp) factory.timestamp();
            return factory.build();
        }

        @Override
        public void submit(final RecipientList recipients, final Message message) {
            final Message.Confirmation confirmation = message.deliver(recipients);
            this.logger.log(confirmation.toLogRecord());
        }

        @Override
        public void send(final CommandSender sender, final String pattern, final Object... arguments) {
            final RecipientList recipients = RecipientList.Sender.create(sender);
            final Message message = this.draft(pattern, arguments);
            this.submit(recipients, message);
        }

        @Override
        public void broadcast(final String pattern, final Object... arguments) {
            final RecipientList recipients = RecipientList.ServerPlayers.create(this.server);
            final Message message = this.draft(pattern, arguments);
            this.submit(recipients, message);
        }

        @Override
        public void announce(final World world, final String pattern, final Object... arguments) {
            final RecipientList recipients = RecipientList.WorldPlayers.create(world);
            final Message message = this.draft(pattern, arguments);
            this.submit(recipients, message);
        }

        @Override
        public void publish(final String permission, final String pattern, final Object... arguments) {
            final RecipientList recipients = RecipientList.Subscribers.create(this.server.getPluginManager(), permission);
            final Message message = this.draft(pattern, arguments);
            this.submit(recipients, message);
        }





        public static class Factory {

            public static PatternCourier.Factory create(final Logger logger, final Server server) {
                return new Factory().setLogger(logger).setServer(server);
            }



            protected Logger logger;
            protected Server server;
            protected boolean timestamp = PatternCourier.DEFAULT_TIMESTAMP;

            public Factory() {}

            /** @param logger where to report events */
            public PatternCourier.Factory setLogger(final Logger logger) {
                this.logger = logger;
                return this;
            }

            /** @param server used for recipient definitions */
            public PatternCourier.Factory setServer(final Server server) {
                this.server = server;
                return this;
            }

            /** @param timestamp true to prepend timestamps to messages */
            public PatternCourier.Factory setTimestamp(final boolean timestamp) {
                this.timestamp = true;
                return this;
            }

            public PatternCourier build() {
                return new PatternCourier(this);
            }

        }

    }





    /**
     * message content patterns are referenced by configuration keys
     * @version 9.0.0
     */
    public static class ConfigurationCourier extends PatternCourier {

        protected static final char DEFAULT_FORMAT_CODE = ChatColor.COLOR_CHAR;

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

        /** @return prefix that designates format code in message patterns */
        public char getFormatCode() {
            return this.formatCode;
        }

        /**
         * retrieve pattern at key and translate alternate codes into Minecraft formatting codes
         * @param key path relative to {@link #getBase base} that contains
         * message pattern
         * @return null if key does not exist or is empty
         */
        public String translate(final String key) {
            final String pattern = this.base.getString(key);
            if (pattern == null || pattern.length() == 0) {
                final String path = this.base.getCurrentPath();
                this.logger.log(Level.FINEST, "String value not found for {1,choice,0#|1#{2}{3}}{0}", new Object[] { key, path.length(), path, this.base.getRoot().options().pathSeparator() });
                return null;
            }

            return ChatColor.translateAlternateColorCodes(this.formatCode, pattern);
        }

        /**
         * retrieve a message pattern from the configuration and format with
         * supplied arguments
         * @param key path relative to {@link #getBase base} that contains
         * message pattern
         * @return null if key does not exist or is empty
         */
        @Override
        public String format(final String key, final Object... arguments) {
            final String pattern = this.translate(key);
            if (pattern == null) return null;
            return super.format(pattern, arguments);
        }

        /**
         * {@inheritDoc}
         * @param key path relative to {@link #getBase base} that contains
         * message pattern
         * @return null if key does not exist or is empty
         */
        @Override
        public Message draft(final String key, final Object... arguments) {
            final String pattern = this.translate(key);
            if (pattern == null) return null;
            return super.draft(pattern, arguments);
        }

        /**
         * {@inheritDoc}
         * @param key path relative to {@link #getBase base} that contains
         * message pattern (null and empty strings patterns are silently
         * ignored and not sent)
         */
        @Override
        public void send(final CommandSender sender, final String pattern, final Object... arguments) {
            final Message message = this.draft(pattern, arguments);
            if (message == null) return;
            final RecipientList recipients = RecipientList.Sender.create(sender);
            this.submit(recipients, message);
        }

        /**
         * {@inheritDoc}
         * @param key path relative to {@link #getBase base} that contains
         * message pattern (null and missing patterns are silently ignored
         * and not sent)
         */
        @Override
        public void broadcast(final String pattern, final Object... arguments) {
            final Message message = this.draft(pattern, arguments);
            if (message == null) return;
            final RecipientList recipients = RecipientList.ServerPlayers.create(this.server);
            this.submit(recipients, message);
        }

        /**
         * {@inheritDoc}
         * @param key path relative to {@link #getBase base} that contains
         * message pattern (null and missing patterns are silently ignored
         * and not sent)
         */
        @Override
        public void announce(final World world, final String pattern, final Object... arguments) {
            final Message message = this.draft(pattern, arguments);
            if (message == null) return;
            final RecipientList recipients = RecipientList.WorldPlayers.create(world);
            this.submit(recipients, message);
        }

        /**
         * {@inheritDoc}
         * @param key path relative to {@link #getBase base} that contains
         * message pattern (null and missing patterns are silently ignored
         * and not sent)
         */
        @Override
        public void publish(final String permission, final String pattern, final Object... arguments) {
            final Message message = this.draft(pattern, arguments);
            if (message == null) return;
            final RecipientList recipients = RecipientList.Subscribers.create(this.server.getPluginManager(), permission);
            this.submit(recipients, message);
        }





        public static class Factory extends PatternCourier.Factory {

            /**
             * prepends a timestamp to all messages and retrieves message
             * patterns from plugin root configuration
             */
            public static ConfigurationCourier.Factory create(final Plugin plugin) {
                return new ConfigurationCourier.Factory()
                    .setLogger(plugin.getLogger())
                    .setServer(plugin.getServer())
                    .setBase(plugin.getConfig());
            }

            protected ConfigurationSection base;
            protected char formatCode;

            public Factory() {}

            /** @param section base section containing message patterns */
            public ConfigurationCourier.Factory setBase(final ConfigurationSection section) {
                if (section == null) throw new IllegalArgumentException("ConfigurationSection can not be null");
                this.base = section;
                return this;
            }

            /** @param path relative to current base for message patterns */
            public ConfigurationCourier.Factory setPath(final String path) {
                final ConfigurationSection section = this.base.getConfigurationSection(path);
                if (section == null) throw new IllegalArgumentException("ConfigurationSection not found: " + path);
                this.setBase(section);
                return this;
            }

            /** @param key path to format code prefix character in base */
            public ConfigurationCourier.Factory setFormatCode(final String key) {
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
            public ConfigurationCourier.Factory setFormatCode(final char formatCode) {
                this.formatCode = formatCode;
                return this;
            }

            @Override
            public ConfigurationCourier build() {
                return new ConfigurationCourier(this);
            }

            @Override
            public ConfigurationCourier.Factory setLogger(final Logger logger) {
                super.setLogger(logger);
                return this;
            }

            @Override
            public ConfigurationCourier.Factory setServer(final Server server) {
                super.setServer(server);
                return this;
            }

            @Override
            public ConfigurationCourier.Factory setTimestamp(final boolean timestamp) {
                super.setTimestamp(timestamp);
                return this;
            }

        }

    }

}
