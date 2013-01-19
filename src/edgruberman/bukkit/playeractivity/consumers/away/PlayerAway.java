package edgruberman.bukkit.playeractivity.consumers.away;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerAway extends PlayerEvent {

    private final long since;
    private final String reason;

    public PlayerAway(final Player who, final long since, final String reason) {
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
        return PlayerAway.handlers;
    }

    public static HandlerList getHandlerList() {
        return PlayerAway.handlers;
    }

}
