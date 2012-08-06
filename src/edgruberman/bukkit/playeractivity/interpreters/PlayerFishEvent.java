package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PlayerFishEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.player.PlayerFishEvent event) {
        this.record(event.getPlayer(), event);
    }

}
