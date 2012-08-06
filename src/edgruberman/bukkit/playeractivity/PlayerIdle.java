package edgruberman.bukkit.playeractivity;

import org.bukkit.entity.Player;

/** player status indicating player has not recorded any monitored activity for a period */
public class PlayerIdle extends PlayerStatus {

    /** the difference, measured in milliseconds, between the time the player went idle and the player's last monitored activity */
    public final long duration;

    public PlayerIdle(final Player player, final long last, final long occurred, final long duration) {
        super(player, last, occurred);
        this.duration = duration;
    }

}
