package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class EnchantItemEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.enchantment.EnchantItemEvent event) {
        this.player = event.getEnchanter();
    }

}
