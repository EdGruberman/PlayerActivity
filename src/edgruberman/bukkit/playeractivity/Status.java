package edgruberman.bukkit.playeractivity;

import org.bukkit.entity.Player;

public class Status {

    private final Player player;
    private Long lastActivity = null;

    boolean isWarned = false;

    Status(final Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Long getLastActivity() {
        return this.lastActivity;
    }

    public void setLastActivity(final long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Long idleFor() {
        if (this.lastActivity == null) return null;

        return (System.currentTimeMillis() - this.lastActivity);
    }

}
