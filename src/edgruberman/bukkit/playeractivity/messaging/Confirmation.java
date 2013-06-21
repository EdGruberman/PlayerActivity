package edgruberman.bukkit.playeractivity.messaging;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.bukkit.command.CommandSender;

/**
 * summary of {@link Message} delivery
 * @author EdGruberman (ed@rjump.com)
 * @version 3.0.0
 */
public class Confirmation {

    protected final String pattern;

    protected final Object[] arguments;

    /** visibility of log entry */
    protected final Level level;

    /** count of recipients message was delivered to */
    protected final List<CommandSender> received;

    /**
     * @param level visibility of log entry
     * @param received count of recipients message was delivered to
     * @param pattern {@link java.text.MessageFormat MessageFormat} pattern
     * @param arguments pattern arguments
     */
    public Confirmation(final Level level, final List<CommandSender> received, final String pattern, final Object... arguments) {
        this.pattern = pattern;
        this.arguments = arguments;
        this.level = level;
        this.received = received;
    }

    /** visibility of log entry */
    public Level getLevel() {
        return this.level;
    }

    /** count of recipients message was delivered to */
    public List<CommandSender> getReceived() {
        return Collections.unmodifiableList(this.received);
    }

    /** lazy log record */
    public LogRecord toLogRecord() {
        final LogRecord record = new LogRecord(this.level, this.pattern);
        record.setParameters(this.arguments);
        return record;
    }

}
