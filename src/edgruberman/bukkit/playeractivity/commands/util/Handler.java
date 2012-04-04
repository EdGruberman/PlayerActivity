package edgruberman.bukkit.playeractivity.commands.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Message;

/**
 * Command execution manager
 */
public class Handler implements CommandExecutor  {

    public JavaPlugin plugin;
    public PluginCommand command;
    public String permission;
    public List<Action> actions = new ArrayList<Action>();

    /**
     * Create a command executor with a default plugin.command permission.
     *
     * @param plugin command owner
     * @param name command name
     * @param reference plugin.yml section name containing command definition
     */
    public Handler(final JavaPlugin plugin, final String name, final String reference) {
        this.plugin = plugin;
        this.setExecutorOf(name, reference);
        this.permission = this.command.getPlugin().getDescription().getName().toLowerCase() + "." + this.command.getName();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        this.command.getPlugin().getLogger().log(Level.FINER, sender.getName() + " issued command: " + label + " " + Arrays.toString(args));

        final Context context = new Context(this, sender, label, args);
        if (!context.action.isAllowed(context)) return true;

        if (context.action.perform(context)) return true;

        // Send usage information on error
        for (final String line : context.action.handler.command.getUsage().replace("<command>", context.label).split("\n"))
            Message.manager.send(context.sender, line, MessageLevel.NOTICE, false);

        return true; // Always tell Bukkit this is successful as usage message errors are handled internally
    }

    public void setDefaultAction(final Action action) {
        this.actions.remove(action);
        this.actions.add(0, action);
    }

    public Action getDefaultAction() {
        return this.actions.get(0);
    }

    /**
     * Registers executor for a command.
     *
     * @param name command to register
     */
    private void setExecutorOf(final String name, final String reference) {
        this.command = this.getCommand(name, reference);
        if (this.command == null) {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to register command: " + name);
            return;
        }

        if (!this.command.getName().equals(name))
            this.plugin.getLogger().warning("Command conflict for /" + name + "; registered as " + this.command.getName());

        this.command.setExecutor(this);
    }

    @Override
    public String toString() {
        return "Handler [command=" + this.command.getLabel() + "]";
    }

    /**
     * Get command that matches name for this plugin, or create a new command.
     *
     * @param name command name
     * @param reference plugin.yml section name that contains command definition
     * @return already registered command or newly created command
     */
    private PluginCommand getCommand(final String name, final String reference) {
        PluginCommand command = this.plugin.getCommand(name);
        if (command != null) return command;

        command = this.plugin.getCommand(this.plugin.getName() + ":" + name);
        if (command != null) return command;

        try {
            command = this.createCommand(name);

        } catch (final Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to create command: " + name, e);
            return null;
        }

        final YamlConfiguration descriptionFile = YamlConfiguration.loadConfiguration(this.plugin.getClass().getResourceAsStream("/plugin.yml"));

        final ConfigurationSection section = descriptionFile.getConfigurationSection("command-reference");
        if (section == null) return command;

        final ConfigurationSection definition = section.getConfigurationSection(reference);
        if (definition == null) return command;

        if (definition.isString("description")) command.setDescription(definition.getString("description"));
        if (definition.isString("usage")) command.setUsage(definition.getString("usage"));
        if (definition.isList("aliases")) command.setAliases(definition.getStringList("aliases"));
        if (definition.isString("permission")) command.setPermission(definition.getString("permission"));
        if (definition.isString("permission-message")) command.setPermissionMessage(definition.getString("permission-message"));

        ((CraftServer) this.plugin.getServer()).getCommandMap().register(this.plugin.getName(), command);

        return command;
    }

    private PluginCommand createCommand(final String name) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Constructor<PluginCommand> ct = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        ct.setAccessible(true);
        return ct.newInstance(name, this.plugin);
    }

    /**
     * Unregister this command.
     *
     * @return true if command was unregistered; false otherwise
     */
    public boolean unregister() {
        return this.command.unregister(((CraftServer) this.command.getPlugin().getServer()).getCommandMap());
    }

}
