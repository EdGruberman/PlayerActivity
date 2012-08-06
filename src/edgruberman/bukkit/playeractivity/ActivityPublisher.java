package edgruberman.bukkit.playeractivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public final class ActivityPublisher extends Observable {

    final Map<Player, Long> last = new HashMap<Player, Long>();

    void record(final Player player, final Class<? extends Event> event) {
        final long occurred = System.currentTimeMillis();
        final Long last = this.last.put(player, occurred);
        this.publish(player, event, occurred, last);
    }

    void publish(final Player player, final Class<? extends Event> event, final long occurred, final Long last) {
        if (this.countObservers() == 0) return;

        this.setChanged();
        this.notifyObservers(new PlayerActive(player, last, occurred, event));
    }

    void clear() {
        this.deleteObservers();
        this.last.clear();
    }

}
