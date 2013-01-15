package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class EnchantItemEvent extends Interpreter {

    public EnchantItemEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.enchantment.EnchantItemEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.enchantment.EnchantItemEvent sub = (org.bukkit.event.enchantment.EnchantItemEvent) event;
        if (!(sub.getEnchanter() instanceof Player)) return;

        this.record(sub.getEnchanter(), event);
    }

}
