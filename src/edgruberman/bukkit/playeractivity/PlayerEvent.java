package edgruberman.bukkit.playeractivity;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PlayerEvent {

    public final Player player;
    public final long occurred;
    public final Event event;

    PlayerEvent(final Player player, final Event event, final long occurred) {
        this.player = player;
        this.event = event;
        this.occurred = occurred;
    }

}
