package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PlayerMoveBlockEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final edgruberman.bukkit.playeractivity.PlayerMoveBlockEvent event) {
        this.record(event.getPlayer(), event);
    }

}
