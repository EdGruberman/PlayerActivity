package edgruberman.bukkit.playeractivity;

import org.bukkit.entity.Player;

public class PlayerStatus {

    /** player status relates to */
    public final Player player;

    /** the difference, measured in milliseconds, between the time any monitored activity previously occurred for the player and midnight, January 1, 1970 UTC */
    public final Long last;

    /** the difference, measured in milliseconds, between the time this status happened and midnight, January 1, 1970 UTC */
    public final long occurred;

    protected PlayerStatus(final Player player, final Long last, final long occurred) {
        this.player = player;
        this.last = last;
        this.occurred = occurred;
    }

}
