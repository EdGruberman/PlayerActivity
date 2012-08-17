package edgruberman.bukkit.playeractivity;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerMoveBlockEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Location from;
    private final Location to;

    public PlayerMoveBlockEvent(final Player who, final Location from, final Location to) {
        super(who);
        this.from = from;
        this.to = to;
    }

    /** location that has a different block than to */
    public Location getFrom() {
        return this.from;
    }

    /** location that has a different block than from */
    public Location getTo() {
        return this.to;
    }

    @Override
    public HandlerList getHandlers() {
        return PlayerMoveBlockEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return PlayerMoveBlockEvent.handlers;
    }

}
