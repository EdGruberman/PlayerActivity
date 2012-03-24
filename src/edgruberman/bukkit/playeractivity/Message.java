package edgruberman.bukkit.playeractivity;

import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageManager;

public class Message {

    public static MessageManager manager;

    Message(final Plugin plugin) {
        Message.manager = new MessageManager(plugin);
    }

}
