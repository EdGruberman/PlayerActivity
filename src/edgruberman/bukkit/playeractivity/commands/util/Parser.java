package edgruberman.bukkit.playeractivity.commands.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Static utility class to consolidate/organize common code for interacting with command line parameters.
 */
public class Parser {

    // TODO prefer online players
    /**
     * Find matching player. Exact player name required if no trailing
     * asterisk. If trailing asterisk, then first player name found that starts
     * with the query will be returned.
     *
     * @param context command execution context
     * @param position position of player name query in command arguments (0 based, not including leading command label)
     * @return player with name matching query; null if no player has ever connected that matches or argument in position does not exist
     */
    public static OfflinePlayer parsePlayer(final Context context, final int position) {
        if (position < 0 || context.arguments.size() <= position) return null;

        String query = context.arguments.get(position).toLowerCase();
        final boolean isExact = !query.endsWith("*"); // Trailing asterisk indicates to use first player name found beginning with characters before asterisk
        if (!isExact) query = query.substring(0, query.length() - 1); // Strip trailing asterisk

        for (final OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
            final String name = (op.getPlayer() != null ? op.getPlayer().getName() : op.getName()).toLowerCase();
            if (isExact) {
                if (name.equals(query)) return op;

                continue;
            }

            // Return first matching player name starting with query characters
            if (name.startsWith(query)) return op;
        }

        return null;
    }

    /**
     * Parse a list of long numbers from the command line in comma delimited form.
     *
     * @param context command execution context
     * @param position position in command arguments (0 based, not including leading command label)
     * @return list of each number that was delimited by a comma; null if argument does not exist, or no numbers specified
     */
    public static List<Long> parseLongList(final Context context, final int position) {
        if (position < 0 || context.arguments.size() <= position) return null;

        final List<Long> values = new ArrayList<Long>();
        for (final String s : context.arguments.get(position).split(","))
            try {
                values.add(Long.parseLong(s));
            } catch (final Exception e) {
                continue;
            }
        if (values.size() == 0) return null;

        return values;
    }

    /**
     * Concatenate a collection with a space between each entry.
     *
     * @param col entries to concatenate
     * @return entries concatenated; empty string if no entries
     */
    public static String join(final Collection<? extends String> col) {
        return Parser.join(col, " ");
    }

    /**
     * Concatenate a collection with a delimiter.
     *
     * @param col entries to concatenate
     * @param delim placed between each entry
     * @return entries concatenated; empty string if no entries
     */
    public static String join(final Collection<? extends String> col, final String delim) {
        if (col == null || col.isEmpty()) return "";

        final StringBuilder sb = new StringBuilder();
        for (final String s : col) sb.append(s + delim);
        sb.delete(sb.length() - delim.length(), sb.length());

        return sb.toString();
    }

    /**
     * Verifies if argument can be converted to an integer and returns it if so.
     *
     * @param context command execution context
     * @param position position of player name query in command arguments (0 based, not including leading command label)
     * @return integer value or null if position does not exist or argument can not be converted
     */
    public static Integer getInteger(final Context context, final int position) {
        if (position < 0 || context.arguments.size() <= position) return null;

        if (!Parser.isInteger(context.arguments.get(position))) return null;

        return Integer.parseInt(context.arguments.get(position));
    }

    public static boolean isInteger(final String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch(final Exception e) {
            return false;
        }
    }

    /**
     * Verifies if argument can be converted to an double and returns it if so.
     *
     * @param context command execution context
     * @param position position of player name query in command arguments (0 based, not including leading command label)
     * @return double value or null if position does not exist or argument can not be converted
     */
    public static Double getDouble(final Context context, final int position) {
        if (position < 0 || context.arguments.size() <= position) return null;

        if (!Parser.isDouble(context.arguments.get(position))) return null;

        return Double.parseDouble(context.arguments.get(position));
    }

    public static boolean isDouble(final String s) {
        try {
            Double.parseDouble(s);
            return true;
        }
        catch(final Exception e) {
            return false;
        }
    }

    /**
     * Parse an ISO8601 formatted date/time string.
     *
     * @param text string representation of date/time
     * @return parsed date/time value
     */
    public static Date parseDate(final Context context, final int position) {
        if (position < 0 || context.arguments.size() <= position) return null;

        return Parser.parseDate(context, position, "yyyy-MM-dd'T'HH:mm:ssz");
    }

    /**
     * Parse a date/time formatted string.
     *
     * @param text string representation of date/time
     * @param pattern pattern for date/time text is formatted as
     * @return parsed date/time value
     */
    public static Date parseDate(final Context context, final int position, final String pattern) {
        if (position < 0 || context.arguments.size() <= position) return null;

        Date date = null;
        try {
            date = (new SimpleDateFormat(pattern)).parse(context.arguments.get(position));
        } catch (final ParseException e) {
            // ignore to return null
        }
        return date;
    }

    public static String parseString(final Context context, final int position) {
        if (position < 0 || context.arguments.size() <= position) return null;

        return context.arguments.get(position);
    }

}
