package edgruberman.bukkit.playeractivity;

import org.bukkit.entity.Player;

public class PlayerEvent {

    private final Player player;
    private long occurred;

    PlayerEvent(final Player player, final long occurred) {
        this.player = player;
        this.occurred = occurred;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Long getOccurred() {
        return this.occurred;
    }

    public void setOccurred(final long occurred) {
        this.occurred = occurred;
    }

}
