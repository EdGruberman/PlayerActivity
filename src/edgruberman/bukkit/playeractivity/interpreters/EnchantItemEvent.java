package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class EnchantItemEvent extends Interpreter {

    public EnchantItemEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.enchantment.EnchantItemEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.enchantment.EnchantItemEvent sub = (org.bukkit.event.enchantment.EnchantItemEvent) event;
        if (!(sub.getEnchanter() instanceof Player)) return;

        this.record(sub.getEnchanter(), event);
    }

}
