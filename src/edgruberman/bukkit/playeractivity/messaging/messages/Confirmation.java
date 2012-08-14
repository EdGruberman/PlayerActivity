package edgruberman.bukkit.playeractivity.messaging.messages;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import edgruberman.bukkit.playeractivity.messaging.Message;

/**
 * summary of {@link Message} delivery
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 1.0.0
 */
public class Confirmation extends Message {

    private static final long serialVersionUID = 1L;

    /** visibility of log entry */
    protected final Level level;

    /** count of recipients message was delivered to */
    protected final int received;

    /**
     * create a delivery summary
     *
     * @param level visibility of log entry
     * @param received count of recipients message was delivered to
     * @param pattern {@link java.text.MessageFormat MessageFormat} pattern
     * @param arguments pattern arguments
     */
    public Confirmation(final Level level, final int received, final String pattern, final Object... arguments) {
        super(pattern, arguments);
        this.level = level;
        this.received = received;
    }

    /** visibility of log entry */
    public Level getLevel() {
        return this.level;
    }

    /** count of recipients message was delivered to */
    public int getReceived() {
        return this.received;
    }

    /** lazy log record */
    public LogRecord toLogRecord() {
        final LogRecord record = new LogRecord(this.level, this.original);
        record.setParameters(this.arguments);
        return record;
    }

}
