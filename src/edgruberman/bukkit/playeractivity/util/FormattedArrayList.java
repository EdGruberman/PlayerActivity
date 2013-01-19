package edgruberman.bukkit.playeractivity.util;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.configuration.ConfigurationSection;

public class FormattedArrayList extends ArrayList<Object> {

    private static final String DEFAULT_FORMAT = "{0}";
    private static final String DEFAULT_ITEM = "{0}";
    private static final String DEFAULT_DELIMITER = " ";

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
        this(config.getString("+format", FormattedArrayList.DEFAULT_FORMAT)
                , config.getString("+item", FormattedArrayList.DEFAULT_ITEM)
                , config.getString("+delimiter", FormattedArrayList.DEFAULT_DELIMITER));
    }

    public boolean add(final Object... arguments) {
        return this.add(Arrays.asList(arguments));
    }

    @Override
    public String toString() {
        final Iterator<Object> i = this.iterator();
        if (!i.hasNext()) return MessageFormat.format(this.format, "");

        final StringBuilder items = new StringBuilder();
        while (i.hasNext()) {
            final Object o = i.next();
            if (o == this) {
                items.append("{this}");
                continue;
            }
            final MessageFormat message = new MessageFormat(this.item);
            items.append(message.format((o instanceof Collection ? ((Collection<?>) o).toArray() : o), new StringBuffer(), new FieldPosition(0)));
            if (i.hasNext()) items.append(this.delimiter);
        }
        return MessageFormat.format(this.format, items);
    }

}
