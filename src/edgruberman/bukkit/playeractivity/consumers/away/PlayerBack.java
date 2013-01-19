package edgruberman.bukkit.playeractivity.consumers.away;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerBack extends PlayerEvent {

    private final long since;
    private final String reason;

    public PlayerBack(final Player who, final long since, final String reason) {
        super(who);
        this.since = since;
        this.reason = reason;
    }

    public long getSince() {
        return this.since;
    }

    public String getReason() {
        return this.reason;
    }

    // ---- Custom Event Handlers ----

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return PlayerBack.handlers;
    }

    public static HandlerList getHandlerList() {
        return PlayerBack.handlers;
    }

}
