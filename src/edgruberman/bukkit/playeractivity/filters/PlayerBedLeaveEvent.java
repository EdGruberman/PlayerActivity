package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerBedLeaveEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerBedLeaveEvent event) {
        this.player = event.getPlayer();
    }

}
