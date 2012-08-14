package edgruberman.bukkit.playeractivity.messaging.messages;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.command.CommandSender;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import edgruberman.bukkit.playeractivity.messaging.Message;

/**
 * prepends a timestamp argument who's time zone is customized for target
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 1.0.0
 */
public class TimestampedMessage extends Message {

    private static final long serialVersionUID = 1L;

    public static TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    public static TimeZone getTimeZone(final CommandSender sender) {
        if (!(sender instanceof Metadatable))
            return TimestampedMessage.DEFAULT_TIME_ZONE;

        final Metadatable meta = (Metadatable) sender;
        final List<MetadataValue> values = meta.getMetadata("TimeZone");
        if (values.size() == 0)
            return TimestampedMessage.DEFAULT_TIME_ZONE;

        return (TimeZone) values.get(0);
    }

    public TimestampedMessage(final String pattern, final Object... arguments) {
        super(pattern, arguments);
        this.prependTimestamp();
    }

    @Override
    public StringBuffer format(final CommandSender target) {
        final TimeZone timeZone = TimestampedMessage.getTimeZone(target);

        final Object[] formats = this.getFormatsByArgumentIndex();
        if (formats.length >= 1 && formats[0] instanceof SimpleDateFormat) {
            final SimpleDateFormat sdf = (SimpleDateFormat) formats[0];
            sdf.setTimeZone(timeZone);
        }

        return super.format(target);
    }

    private void prependTimestamp() {
        final Object[] prepend = new Object[this.arguments.length + 1];
        prepend[0] = new Date();
        if (this.arguments.length >= 1) System.arraycopy(this.arguments, 0, prepend, 1, this.arguments.length);
        this.arguments = prepend;
    }

}
