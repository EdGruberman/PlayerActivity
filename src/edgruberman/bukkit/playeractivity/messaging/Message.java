package edgruberman.bukkit.playeractivity.messaging;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * {@link java.text.MessageFormat MessageFormat} with customizable arguments for each target
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 1.0.0
 */
public class Message extends MessageFormat {

    private static final long serialVersionUID = 1L;

    /** original pattern */
    protected String original;

    /** arguments to format pattern with upon delivery */
    protected Object[] arguments;

    /**
     * @param pattern {@link java.text.MessageFormat MessageFormat} pattern
     * @param arguments pattern arguments
     */
    public Message(final String pattern, final Object... arguments) {
        super(pattern);
        this.original = pattern;
        this.arguments = arguments;
    }

    /** resolve arguments and apply to pattern adjusting as necessary for target */
    public StringBuffer format(final CommandSender target) {
        return this.format(this.arguments, new StringBuffer(), null);
    }

    /** format message for sending to a generic target */
    @Override
    public String toString() {
        return this.format(Bukkit.getConsoleSender()).toString();
    }

}
