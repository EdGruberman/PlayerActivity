package edgruberman.bukkit.messaging.messages;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import edgruberman.bukkit.messaging.Message;

public class Confirmation extends Message {

    private final Level level;
    private final int received;

    public Confirmation(final Level level, final int received, final String format, final Object... args) {
        super(format, args);
        this.level = level;
        this.received = received;
    }

    public Level getLevel() {
        return this.level;
    }

    public int getReceived() {
        return this.received;
    }

    public LogRecord toLogRecord() {
        return new LogRecord(this.level, this.toString());
    }

}
