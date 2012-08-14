package edgruberman.bukkit.playeractivity.messaging.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import edgruberman.bukkit.playeractivity.messaging.Message;

/**
 * message pattern string pulled from a {@link org.bukkit.configuration.ConfigurationSection ConfigurationSection}
 * (allows for easy user customization using a config.yml file)
 * and prepends a timestamp argument who's time zone is customized for target
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 1.0.0
 */
public class TimestampedConfigurationMessage extends TimestampedMessage {

    private static final long serialVersionUID = 1L;

    // ---- static factory ----

    /** multiple messages will be created, each with the same arguments, if the configuration entry is a list of strings */
    public static List<? extends Message> create(final ConfigurationSection base, final String path, final Object... arguments) {
        if (base.isString(path))
            return Arrays.asList(new TimestampedConfigurationMessage(base, path, arguments));

        if (!base.isList(path)) return Collections.emptyList();

        final List<TimestampedConfigurationMessage> messages = new ArrayList<TimestampedConfigurationMessage>();
        for (final String item : base.getStringList(path))
            messages.add(new TimestampedConfigurationMessage(item, arguments));

        return messages;
    }



    // ---- instance ----

    /** single string entry in configuration; use {@link #create} to allow entry to be either a single string or string list */
    public TimestampedConfigurationMessage(final ConfigurationSection base, final String path, final Object... arguments) {
        super(base.getString(path), arguments);
    }

    /** used internally to pass a single string from a list as an individual message pattern */
    protected TimestampedConfigurationMessage(final String pattern, final Object... arguments) {
        super(pattern, arguments);
    }

}
