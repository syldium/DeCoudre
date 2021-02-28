package me.syldium.thimble.common.command.abstraction;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractCommand {

    protected final String name;
    protected final Permission permission;
    protected final @Nullable MessageKey description;
    protected @Nullable ParentCommand parent;

    AbstractCommand(@NotNull String name, @Nullable MessageKey description, @NotNull Permission permission) {
        this.name = name;
        this.description = description;
        this.permission = permission;
    }

    public void preExecute(@NotNull ThimblePlugin plugin, @NotNull Sender sender) throws CommandException {

    }

    public abstract @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args, @NotNull String label);

    public abstract @NotNull List<@NotNull String> tabComplete(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args);

    public @NotNull String getName() {
        return this.name;
    }

    protected @NotNull String getPath() {
        if (this.parent != null) {
            return this.parent.getPath() + " " + this.name;
        }
        return this.name;
    }

    public abstract @NotNull Component getHelp(@NotNull MessageService service);

    public abstract @NotNull Component getUsage(@NotNull MessageService service);

    public boolean hasPermission(@NotNull Sender sender) {
        return sender.hasPermission(this.permission.get());
    }

    public @NotNull String getPermission() {
        return this.permission.get();
    }

    public boolean isValidExecutor(@NotNull Sender sender) {
        return true;
    }

    public abstract int getMinArgumentCount();

    public abstract @NotNull AbstractCommand get(@NotNull List<@NotNull String> args);

    public abstract <T extends AbstractCommand> @Nullable T lookup(@NotNull Class<T> clazz);

    public @Nullable ParentCommand getParent() {
        return this.parent;
    }

    /**
     * Gets if this command should be displayed by tab completion.
     *
     * @return If the command should be displayed.
     */
    public boolean shouldDisplay() {
        return true;
    }
}
