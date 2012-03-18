package edgruberman.bukkit.playeractivity;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Represents a player related activity that occurred.
 */
public class PlayerActivity {

    public final Player player;
    public final long occurred;
    public final Event event;
    public final Long last;

    PlayerActivity(final Player player, final Event event, final long occurred, final Long last) {
        this.player = player;
        this.event = event;
        this.occurred = occurred;
        this.last = last;
    }

}
