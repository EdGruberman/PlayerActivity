package edgruberman.bukkit.playeractivity.consumers;

import org.bukkit.entity.Player;

public class AwayState {

    public final Player player;
    public final long since;
    public final String reason;

    AwayState(final Player player, final long since, final String reason) {
        this.player = player;
        this.since = since;
        this.reason = reason;
    }

}
