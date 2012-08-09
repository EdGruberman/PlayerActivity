package edgruberman.bukkit.messaging.messages;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.command.CommandSender;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import edgruberman.bukkit.messaging.Message;

public class TimestampedMessage extends Message {

    public static TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    public TimestampedMessage(final String format, final Object... args) {
        super(format, TimestampedMessage.prependTimestamp(args));
    }

    @Override
    public String formatFor(final CommandSender target) {
        final TimeZone timeZone = TimestampedMessage.getTimeZone(target);
        ((Calendar) this.args[0]).setTimeZone(timeZone);
        return super.formatFor(target);
    }

    public static Object[] prependTimestamp(final Object... args) {
        final Object[] prepend = new Object[args.length + 1];
        prepend[0] = new GregorianCalendar(TimestampedMessage.DEFAULT_TIME_ZONE);
        if (args.length >= 1) System.arraycopy(args, 0, prepend, 1, args.length);
        return prepend;
    }

    public static TimeZone getTimeZone(final CommandSender sender) {
        if (!(sender instanceof Metadatable))
            return TimestampedMessage.DEFAULT_TIME_ZONE;

        final Metadatable meta = (Metadatable) sender;
        final List<MetadataValue> values = meta.getMetadata("TimeZone");
        if (values.size() == 0)
            return TimestampedMessage.DEFAULT_TIME_ZONE;

        return (TimeZone) values.get(0);
    }

}
