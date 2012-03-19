package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class ProjectileHitEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.entity.ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        this.player = (Player) event.getEntity().getShooter();
    }

}
