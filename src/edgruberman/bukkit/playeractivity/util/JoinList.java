package edgruberman.bukkit.playeractivity.util;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * store parameters for lazy MessageFormat formatting
 * @author EdGruberman (ed@rjump.com)
 * @version 2.0.1
 */
public class JoinList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 1L;



    // ---- defaults ----

    public static final String DEFAULT_FORMAT = "{0}";
    public static final String DEFAULT_ITEM = "{0}";
    public static final String DEFAULT_DELIMITER = " ";
    public static final String DEFAULT_LAST = JoinList.DEFAULT_DELIMITER;



    // ---- convenience functions ----

    public static <T> DefaultFactory<T> factory() {
        return JoinList.DefaultFactory.create();
    }

    /** concatenate with default delimiter of {@value #DEFAULT_DELIMITER} */
    public static String join(final Collection<?> elements) {
        return JoinList.factory().elements(elements).join();
    }

    /** concatenate with default delimiter of {@value #DEFAULT_DELIMITER} */
    public static <T> String join(final T... elements) {
        return JoinList.join(Arrays.asList(elements));
    }



    // ---- instance ----

    private final String format;
    private final String item;
    private final String delimiter;
    private final String last;

    public JoinList() {
        this(JoinList.DEFAULT_FORMAT, JoinList.DEFAULT_ITEM, JoinList.DEFAULT_DELIMITER, JoinList.DEFAULT_LAST);
    }

    public JoinList(final String format, final String item, final String delimiter, final String last) {
        this.format = format;
        this.item = item;
        this.delimiter = delimiter;
        this.last = last;
    }

    protected JoinList(final JoinList.Factory<?, ?> factory) {
        super( factory.elements.size() > 0 ? factory.elements.size() : 10 );
        this.format = factory.format;
        this.item = factory.item;
        this.delimiter = factory.delimiter;
        this.last = factory.last;
    }

    /** add arguments as single element of array */
    public boolean add(final Object... arguments) {
        return this.add((Object) arguments);
    }

    /** format elements and concatenate */
    public String join() {
        final StringBuilder result = new StringBuilder();

        final int last = this.size() - 1;
        for (int i = 0; i < this.size(); i++) {
            final E element = this.get(i);

            // prefix delimiter
            if (result.length() > 0) {
                if (i < last) {
                    result.append(this.delimiter);
                } else {
                    result.append(this.last);
                }
            }

            // prevent recursion
            if (element == this) {
                result.append("{this}");
                continue;
            }

            // format item, which could either be an array of objects or a single object
            final MessageFormat message = new MessageFormat(this.item);
            final Object[] arguments = ( element instanceof Object[] ? (Object[]) element : new Object[] { element } );
            final StringBuffer sb = message.format(arguments, new StringBuffer(), new FieldPosition(0));
            result.append(sb);
        }

        return MessageFormat.format(this.format, result);
    }

    @Override
    public String toString() {
        return this.join();
    }





    public abstract static class Factory<T, F extends JoinList.Factory<T, F>> {

        protected String format = JoinList.DEFAULT_FORMAT;
        protected String item = JoinList.DEFAULT_ITEM;
        protected String delimiter = JoinList.DEFAULT_DELIMITER;
        protected String last = JoinList.DEFAULT_LAST;

        protected Collection<? extends T> elements = Collections.emptyList();

        /**
         * @param format pattern used to format resultant joined output
         * (default is {@value #DEFAULT_FORMAT})
         */
        public F format(final String format) {
            this.format = format;
            return this.cast();
        }

        /**
         * @param item pattern used to format each element in list as
         * parameters (default is {@value #DEFAULT_ITEM})
         */
        public F item(final String item) {
            this.item = item;
            return this.cast();
        }

        /**
         * @param delimiter appended between all items except before the last
         * (default is {@value #DEFAULT_DELIMITER})
         */
        public F delimiter(final String delimiter) {
            this.delimiter = delimiter;
            return this.cast();
        }

        /**
         * @param last delimiter appended before last item
         * (default is {@value #DEFAULT_LAST})
         */
        public F last(final String last) {
            this.last = last;
            return this.cast();
        }

        /**
         * @param elements initialize list with elements
         */
        public F elements(final Collection<? extends T> elements) {
            this.elements = elements;
            return this.cast();
        }

        protected abstract F cast();

        public JoinList<T> build() {
            final JoinList<T> result = new JoinList<T>(this);
            result.addAll(this.elements);
            return result;
        }

        public String join() {
            return this.build().join();
        }

    }





    public static class DefaultFactory<T> extends JoinList.Factory<T, DefaultFactory<T>> {

        public static <Y> DefaultFactory<Y> create() {
            return new DefaultFactory<Y>();
        }

        @Override
        protected DefaultFactory<T> cast() {
            return this;
        }

    }

}
