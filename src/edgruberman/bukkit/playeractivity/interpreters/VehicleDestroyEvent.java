package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class VehicleDestroyEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.vehicle.VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) return;

        this.player = (Player) event.getAttacker();
    }

}
