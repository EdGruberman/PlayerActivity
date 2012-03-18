package edgruberman.bukkit.playeractivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public final class ActivityPublisher extends Observable {

    final Map<Player, Long> last = new HashMap<Player, Long>();

    /**
     * Record last activity for player.
     * (This could be called on high frequency events such as PlayerMoveEvent.)
     *
     * @param player player to record this as last activity for
     * @param type event type that player engaged in
     */
    public void record(final Player player, final Event event) {
        final long occurred = System.currentTimeMillis();
        final Long last = this.last.put(player, occurred);
        this.publish(player, event, occurred, last);
    }

    void publish(final Player player, final Event event, final long occurred, final Long last) {
        if (this.countObservers() == 0) return;

        this.setChanged();
        this.notifyObservers(new PlayerActivity(player, event, occurred, last));
    }

}
