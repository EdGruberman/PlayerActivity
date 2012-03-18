package edgruberman.bukkit.playeractivity;

import org.bukkit.entity.Player;

/**
 * Represents a player that has not recorded any monitored activity for a period.
 */
public class PlayerIdle {

    public final Player player;
    public final long last;
    public final long duration;

    PlayerIdle(final Player player, final long last, final long duration) {
        this.player = player;
        this.last = last;
        this.duration = duration;
    }

}
