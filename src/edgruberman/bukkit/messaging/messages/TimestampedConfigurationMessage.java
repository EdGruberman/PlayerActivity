package edgruberman.bukkit.messaging.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import edgruberman.bukkit.messaging.Message;

public class TimestampedConfigurationMessage extends ConfigurationMessage {

    // ---- Static Factory ----

    public static List<? extends Message> create(final ConfigurationSection base, final String path, final Object... args) {
        if (!base.isList(path))
            return Arrays.asList(new TimestampedConfigurationMessage(base, path, args));

        final List<TimestampedConfigurationMessage> messages = new ArrayList<TimestampedConfigurationMessage>();
        for (final String item : base.getStringList(path))
            messages.add(new TimestampedConfigurationMessage(item, args));

        return messages;
    }



    // ---- Instance ----

    public TimestampedConfigurationMessage(final ConfigurationSection base, final String path, final Object... args) {
        super(base, path, TimestampedMessage.prependTimestamp(args));
    }

    protected TimestampedConfigurationMessage(final String format, final Object... args) {
        super(format, TimestampedMessage.prependTimestamp(args));
    }

    @Override
    public String formatFor(final CommandSender target) {
        final TimeZone timeZone = TimestampedMessage.getTimeZone(target);
        ((Calendar) this.args[0]).setTimeZone(timeZone);
        return super.formatFor(target);
    }

}
