package edgruberman.bukkit.playeractivity.util;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.configuration.ConfigurationSection;

/**
 * allows object references to be stored for lazy MessageFormat formatting
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 1.5.0
 */
public class JoinList<T> extends ArrayList<T> {

    private static final long serialVersionUID = 1L;

    public static final String CONFIG_KEY_FORMAT = "format";
    public static final String CONFIG_KEY_ITEM = "item";
    public static final String CONFIG_KEY_DELIMITER = "delimiter";

    public static final String DEFAULT_FORMAT = "{0}";
    public static final String DEFAULT_ITEM = "{0}";
    public static final String DEFAULT_DELIMITER = " ";



    public static String join(final Collection<?> collection) {
        return JoinList.join(collection, JoinList.DEFAULT_DELIMITER);
    }

    public static String join(final Collection<?> collection, final String delimiter) {
        return JoinList.join(collection, delimiter, JoinList.DEFAULT_ITEM);
    }

    public static String join(final Collection<?> collection, final String delimiter, final String item) {
        return JoinList.join(collection, delimiter, item, JoinList.DEFAULT_FORMAT);
    }

    public static String join(final Collection<?> collection, final String delimiter, final String item, final String format) {
        final JoinList<Object> list = new JoinList<Object>(format, item, delimiter, collection);
        return list.toString();
    }



    private final String format;
    private final String item;
    private final String delimiter;

    public JoinList() {
        this(JoinList.DEFAULT_FORMAT, JoinList.DEFAULT_ITEM, JoinList.DEFAULT_DELIMITER);
    }

    public JoinList(final String format, final String item, final String delimiter) {
        this.format = format;
        this.item = item;
        this.delimiter = delimiter;
    }

    public JoinList(final String format, final String item, final String delimiter, final Collection<? extends T> collection) {
        this(format, item, delimiter);
        this.addAll(collection);
    }

    public JoinList(final ConfigurationSection config) {
        this(config.getString(JoinList.CONFIG_KEY_FORMAT, JoinList.DEFAULT_FORMAT)
                , config.getString(JoinList.CONFIG_KEY_ITEM, JoinList.DEFAULT_ITEM)
                , config.getString(JoinList.CONFIG_KEY_DELIMITER, JoinList.DEFAULT_DELIMITER));
    }

    public JoinList(final ConfigurationSection config, final Collection<? extends T> collection) {
        this(config);
        this.addAll(collection);
    }

    public boolean add(final Object... arguments) {
        return this.add((Object) arguments);
    }

    @Override
    public String toString() {
        final Iterator<T> i = this.iterator();
        if (!i.hasNext()) return MessageFormat.format(this.format, "");

        final StringBuilder items = new StringBuilder();
        while (i.hasNext()) {
            final Object o = i.next();

            // prevent recursion
            if (o == this) {
                items.append("{this}");
                continue;
            }

            // format item, which could either be an array of objects or a single object
            final MessageFormat message = new MessageFormat(this.item);
            final Object[] arguments = ( o instanceof Object[] ? (Object[]) o : new Object[] { o } );
            final StringBuffer sb = message.format(arguments, new StringBuffer(), new FieldPosition(0));
            items.append(sb);

            if (i.hasNext()) items.append(this.delimiter);
        }

        return MessageFormat.format(this.format, items);
    }

}
