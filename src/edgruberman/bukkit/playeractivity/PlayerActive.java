package edgruberman.bukkit.playeractivity;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/** player status indicating monitored activity has been recorded */
public class PlayerActive extends PlayerStatus {

    /** class of the event that triggered the activity */
    public final Class<? extends Event> event;

    public PlayerActive(final Player player, final Long last, final long occurred, final Class<? extends Event> event) {
        super(player, last, occurred);
        this.event = event;
    }

}
