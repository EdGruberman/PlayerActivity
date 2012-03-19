package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PaintingBreakByEntityEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.painting.PaintingBreakByEntityEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getRemover() instanceof Player)) return;

        this.player = (Player) event.getRemover();
    }

}
