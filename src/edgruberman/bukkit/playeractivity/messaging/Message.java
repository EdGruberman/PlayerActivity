package edgruberman.bukkit.playeractivity.messaging;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * {@link java.text.MessageFormat MessageFormat} that sets time zone of each date argument for target
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 2.3.0
 */
public class Message extends MessageFormat {

    private static final long serialVersionUID = 1L;



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

    /** resolve arguments and apply to pattern adjusting as necessary for target */
    public StringBuffer format(final CommandSender target) {
        // format all dates with time zone for target
        TimeZone timeZone = null;
        final Format[] formats = this.getFormatsByArgumentIndex();
        for(int i = 0; i < formats.length; i++) {
            if (!(formats[i] instanceof DateFormat)) continue;

            if (timeZone == null) timeZone = Recipients.getTimeZone(target);
            final DateFormat sdf = (DateFormat) formats[i];
            sdf.setTimeZone(timeZone);
            this.setFormatByArgumentIndex(i, sdf);
        }

        final StringBuffer formatted = this.format(this.arguments, new StringBuffer(), null);
        if (this.suffix != null) formatted.append(this.suffix.format(target));
        return formatted;
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

    public Message getSuffix() {
        return this.suffix;
    }

    /** format message for sending to a generic target */
    @Override
    public String toString() {
        return this.format(Bukkit.getConsoleSender()).toString();
    }



    public static Factory create(final String pattern, final Object... arguments) {
        return Factory.create(pattern, arguments);
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

}
