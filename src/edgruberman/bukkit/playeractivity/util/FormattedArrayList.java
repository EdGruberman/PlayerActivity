package edgruberman.bukkit.playeractivity.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.configuration.ConfigurationSection;

public class FormattedArrayList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 1L;

    private final String format;
    private final String item;
    private final String delimiter;

    public FormattedArrayList(final String format, final String item, final String delimiter) {
        this.format = format;
        this.item = item;
        this.delimiter = delimiter;
    }

    public FormattedArrayList(final ConfigurationSection config) {
        this(config.getString("+format", "{0}"), config.getString("+item", "{0}"), config.getString("+delimiter", " "));
    }

    @Override
    public String toString() {
        final Iterator<E> i = this.iterator();
        if (!i.hasNext()) return MessageFormat.format(this.format, "");

        final StringBuilder items = new StringBuilder();
        while (i.hasNext()) {
            final E e = i.next();
            items.append(e == this ? "{this}" : MessageFormat.format(this.item, e));
            if (i.hasNext()) items.append(this.delimiter);
        }
        return MessageFormat.format(this.format, items);
    }

}
