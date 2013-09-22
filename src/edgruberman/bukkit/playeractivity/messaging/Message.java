package edgruberman.bukkit.playeractivity.messaging;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

/**
 * {@link java.text.MessageFormat MessageFormat} that sets time zone of each date argument for target
 * @author EdGruberman (ed@rjump.com)
 * @version 5.0.0
 */
public class Message extends MessageFormat {

    private static final long serialVersionUID = 1L;

    public static TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    /** retrieve "TimeZone" MetadataValue for CommandSender */
    public static TimeZone getTimeZone(final CommandSender sender) {
        if (!(sender instanceof Metadatable)) return Message.DEFAULT_TIME_ZONE;

        final Metadatable meta = (Metadatable) sender;
        final List<MetadataValue> values = meta.getMetadata("TimeZone");
        if (values.size() == 0) return Message.DEFAULT_TIME_ZONE;

        return (TimeZone) values.get(0).value();
    }



    /** original pattern */
    protected final String original;

    /** arguments to format pattern with upon delivery */
    protected final Object[] arguments;

    protected Message suffix;

    protected Message(final String pattern, final Object... arguments) {
        super(pattern);
        this.original = pattern;
        this.arguments = arguments;
        this.suffix = null;
    }

    public Confirmation deliver(final RecipientList recipients) {
        final List<CommandSender> received = new ArrayList<CommandSender>();
        for (final CommandSender target : recipients) {
            final String formatted = this.format(target).toString();
            target.sendMessage(formatted);
            received.add(target);
        }
        return recipients.confirm(this, received);
    }

    /** resolve arguments and apply to pattern adjusting as necessary for target */
    public StringBuffer format(final CommandSender target) {
        this.updateTimeZones(target);
        final StringBuffer formatted = this.format(this.arguments, new StringBuffer(), null);
        if (this.suffix != null) formatted.append(this.suffix.format(target));
        return formatted;
    }

    /** format all dates with time zone for target */
    protected void updateTimeZones(final CommandSender target) {
        final TimeZone zone = Message.getTimeZone(target);

        final Format[] formats = this.getFormatsByArgumentIndex();
        for(int i = 0; i < formats.length; i++) {
            if (!(formats[i] instanceof DateFormat)) continue;

            final DateFormat sdf = (DateFormat) formats[i];
            sdf.setTimeZone(zone);
            this.setFormatByArgumentIndex(i, sdf);
        }
    }

    /** @param suffix applied to last message in suffix chain to be formatted as a single message */
    public Message append(final Message suffix) {
        if (this.suffix != null) {
            this.suffix.append(suffix);
            return this;
        }

        this.suffix = suffix;
        return this;
    }

    /** @return suffix directly appended to this Message */
    public Message getSuffix() {
        return this.suffix;
    }

    /** @return number of messages, including this one, chained through suffixes */
    public int count() {
        return ( this.suffix != null ? this.suffix.count() + 1 : 1 );
    }

    /** format message for sending to a generic target */
    @Override
    public String toString() {
        return this.format(Bukkit.getConsoleSender()).toString();
    }



    public static class Factory {

        public static Factory create(final String pattern, final Object... arguments) {
            return new Factory(pattern, arguments);
        }

        public String pattern;
        public Object[] arguments;

        protected Factory(final String pattern, final Object... arguments) {
            this.pattern = pattern;
            this.arguments = arguments;
        }

        public Factory timestamp() {
            final Object[] prepend = new Object[this.arguments.length + 1];
            prepend[0] = new Date();
            if (this.arguments.length >= 1) System.arraycopy(this.arguments, 0, prepend, 1, this.arguments.length);
            this.arguments = prepend;
            return this;
        }

        public Message build() {
            return new Message(this.pattern, this.arguments);
        }

    }



    /**
     * summary of {@link Message} delivery
     * @author EdGruberman (ed@rjump.com)
     * @version 4.0.0
     */
    public static class Confirmation {

        protected final String pattern;

        protected final Object[] arguments;

        /** visibility of log entry */
        protected final Level level;

        /** count of recipients message was delivered to */
        protected final List<CommandSender> received;

        /**
         * @param level visibility of log entry
         * @param received count of recipients message was delivered to
         * @param pattern {@link java.text.MessageFormat MessageFormat} pattern
         * @param arguments pattern arguments
         */
        public Confirmation(final Level level, final List<CommandSender> received, final String pattern, final Object... arguments) {
            this.pattern = pattern;
            this.arguments = arguments;
            this.level = level;
            this.received = received;
        }

        /** visibility of log entry */
        public Level getLevel() {
            return this.level;
        }

        /** count of recipients message was delivered to */
        public List<CommandSender> getReceived() {
            return Collections.unmodifiableList(this.received);
        }

        /** lazy log record */
        public LogRecord toLogRecord() {
            final LogRecord record = new LogRecord(this.level, this.pattern);
            record.setParameters(this.arguments);
            return record;
        }

    }



    /**
     * groups multiple {@link Message} instances into pages
     * @author EdGruberman (ed@rjump.com)
     * @version 1.1.0
     */
    public static class Paginator {

        public static final int DEFAULT_PAGE_HEIGHT_PLAYER = 8;
        public static final int DEFAULT_PAGE_HEIGHT_CONSOLE = -1;

        private final List<Message> contents;
        private final int pageSize;

        public Paginator(final List<Message> contents) {
            this(contents, Paginator.DEFAULT_PAGE_HEIGHT_PLAYER);
        }

        public Paginator(final List<Message> contents, final CommandSender target) {
            this(contents, ( target instanceof Player ? Paginator.DEFAULT_PAGE_HEIGHT_PLAYER : Paginator.DEFAULT_PAGE_HEIGHT_CONSOLE ));
        }

        public Paginator(final List<Message> contents, final int pageSize) {
            this.contents = contents;
            this.pageSize = pageSize;
        }

        /** @return number of messages per page */
        public int getPageSize() {
            return this.pageSize;
        }

        public List<Message> getContents() {
            return this.contents;
        }

        /**
         * @param index zero based page number
         * @return messages on page
         */
        public List<Message> page(final int index) {
            if (this.pageSize < 1) {
                if (index != 0) throw new IllegalArgumentException("page index not available: " + index);
                return this.contents;
            }

            final int last = ((index + 1) * this.pageSize) - 1;
            return this.contents.subList(index * this.pageSize, ( last <= this.contents.size() ? last : this.contents.size() ));
        }

        /** @return total number of pages */
        public int count() {
            if (this.pageSize < 1) return 1;
            int messages = 0;
            for (final Message message : this.contents) messages += message.count();
            return (int) Math.ceil((double) messages / this.pageSize);
        }

    }

}
