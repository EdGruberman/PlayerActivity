package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class ProjectileHitEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.entity.ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        this.player = (Player) event.getEntity();
    }

}
