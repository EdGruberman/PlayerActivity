package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class CraftItemEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.inventory.CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            this.isCancelled = true;
            return;
        }

        this.player = (Player) event.getWhoClicked();
    }

}
