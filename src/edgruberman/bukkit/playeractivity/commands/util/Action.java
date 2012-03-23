package edgruberman.bukkit.playeractivity.commands.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;

/**
 * Single action of a command.
 */
public abstract class Action {

    public Handler handler;
    public String name;
    public String permission;
    public Action parent = null;
    public List<Action> children = new ArrayList<Action>();

    /**
     * Register as a single action for an handler which results in a simple
     * command that uses only plugin.command for permission.
     *
     * @param plugin command owner
     * @param label command label
     */
    protected Action(final JavaPlugin plugin, final String label) {
        this(new Handler(plugin, label), label, (String) null);
        this.setDefault();
    }

    /**
     * Multiple actions for a handler which results in a command that uses
     * plugin.command.action permission for each action.
     *
     * @param handler action parent
     * @param name action name (first parameter after command label)
     */
    protected Action(final Handler handler, final String name) {
        this(handler, name, handler.permission + "." + name.toLowerCase());
    }

    /**
     * Register an action for a handler with a custom permission assignment.
     *
     * @param handler action parent
     * @param name action name
     * @param permission custom permission required to use action; null for no permission required
     */
    protected Action(final Handler handler, final String name, final String permission) {
        this.handler = handler;
        this.name = name.toLowerCase();
        this.permission = permission;
        this.handler.actions.add(this);
    }

    /**
     * Register a child action which results in a complex command that appends
     * this action's name to the parent's permission after a period.
     * Example: plugin.command.action.(child^N...).child
     *
     * @param parent
     * @param name
     */
    protected Action(final Action parent, final String name) {
        this(parent, name, parent.permission + "." + name.toLowerCase());
    }

    /**
     * Register a child action with a custom permission.
     *
     * @param parent
     * @param name
     * @param permission custom permission required to use action; null for standard permission
     */
    protected Action(final Action parent, final String name, final String permission) {
        this.parent = parent;
        this.handler = this.parent.handler;
        this.name = name.toLowerCase();
        this.permission = permission;
        this.parent.children.add(this);
    }

    /**
     * Determines if sender is allowed to use the requested action.
     * A message will be sent to the sender if they are not allowed.
     *
     * @param sender
     * @return true if sender is allowed to perform the requested action; false otherwise
     */
    public boolean isAllowed(final Context context) {
        // Check base command permission first
        if ((this.handler.permission != null) && !context.sender.hasPermission(this.handler.permission)) {
            Main.messageManager.send(context.sender, "You are not allowed to use the " + context.label + " command", MessageLevel.RIGHTS, false);
            return false;
        }

        // Check any action specific permission and all parent action permissions
        if (!this.isAllowed(context.sender, this)) return false;

        // Sender is allowed to perform base command, the requested action, and all parent actions
        return true;
    }

    /**
     * Check if the specified action and any parent actions allow the sender to
     * perform the action.
     *
     * @param sender
     * @param action
     * @return true if sender is allowed to perform every action up this lineage
     */
    private boolean isAllowed(final CommandSender sender, final Action action) {
        // Check if sender is allowed to perform specified action
        if ((action.permission != null) && !sender.hasPermission(action.permission)) {
            Main.messageManager.send(sender, "You are not allowed to use the " + this.getNamePath() + " action of the " + this.handler.command.getLabel() + " command", MessageLevel.RIGHTS, false);
            return false;
        }

        // Check if sender is allowed to perform the parent action
        if (action.parent != null)
            return this.isAllowed(sender, action.parent);

        // Sender is allowed to perform specified action and all parent actions
        return true;
    }

    /**
     * Concatenates this action's name along with every parent's name.
     * Example: secondparent firstparent this
     *
     * @return this action's name and all parent's names concatenated with spaces
     */
    public String getNamePath() {
        if (this.parent == null) return this.name;

        return this.parent.getNamePath() + " " + this.name;
    }

    public int getGeneration() {
        return this.getGeneration(0);
    }

    public int getGeneration(final int child) {
        if (this.parent == null) return child;

        return this.parent.getGeneration(child + 1);
    }

    /**
     * Set this action as the command handler's default action.
     */
    public void setDefault() {
        this.handler.setDefaultAction(this);
    }

    /**
     * Determines if this action is applicable to be called for the given
     * arguments based on the first argument matching the action name.
     * Override this method for more complex action assignment.
     *
     * @param context execution context
     * @return true if this action should be performed; false otherwise
     */
    public boolean matches(final Context context)  {
        if (this.handler.actions.size() == 1) return true;

        final int generation = this.getGeneration();

        if (context.arguments.size() <= generation) return false;

        if (context.arguments.get(generation).equalsIgnoreCase(this.name)) return true;

        return false;
    }

    @Override
    public String toString() {
        return "Action [handler=" + this.handler + ", getNamePath()=" + this.getNamePath() + "]";
    }

    /**
     * Performs this action.
     *
     * @param context execution context
     * @return true if the action was performed as expected; false if usage date should be shown
     */
    public abstract boolean perform(final Context context);

}
