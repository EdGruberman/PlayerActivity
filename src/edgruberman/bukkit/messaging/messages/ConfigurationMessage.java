package edgruberman.bukkit.messaging.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import edgruberman.bukkit.messaging.Message;

public class ConfigurationMessage extends Message {

    // ---- Static Factory ----

    public static List<? extends Message> create(final ConfigurationSection base, final String path, final Object... args) {
        if (!base.isList(path))
            return Arrays.asList(new ConfigurationMessage(base, path, args));

        final List<ConfigurationMessage> messages = new ArrayList<ConfigurationMessage>();
        for (final String item : base.getStringList(path))
            messages.add(new ConfigurationMessage(item, args));

        return messages;
    }



    // ---- Instance ----

    public ConfigurationMessage(final ConfigurationSection base, final String path, final Object... args) {
        super(base.getString(path), args);
    }

    protected ConfigurationMessage(final String format, final Object... args) {
        super(format, args);
    }

}
