package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class VehicleDamageEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.vehicle.VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player)) return;

        this.player = (Player) event.getAttacker();
    }

}
