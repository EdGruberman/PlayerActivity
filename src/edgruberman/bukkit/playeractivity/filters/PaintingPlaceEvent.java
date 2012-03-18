package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PaintingPlaceEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.painting.PaintingPlaceEvent event) {
        this.player = event.getPlayer();
    }

}
