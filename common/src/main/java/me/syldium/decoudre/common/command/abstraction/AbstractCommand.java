package me.syldium.decoudre.common.command.abstraction;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.player.Message;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractCommand {

    protected final String name;
    protected final Permission permission;

    protected @Nullable ParentCommand parent;

    AbstractCommand(@NotNull String name, @Nullable Message description, @NotNull Permission permission) {
        this.name = name;
        this.permission = permission;
    }

    public void preExecute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender) throws CommandException {

    }

    public abstract @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException;

    public abstract @NotNull List<@NotNull String> tabComplete(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<String> args);

    public @NotNull String getName() {
        return this.name;
    }

    public abstract @NotNull Component getHelp();

    public abstract @NotNull Component getUsage();

    public boolean hasPermission(@NotNull Sender sender) {
        return sender.hasPermission(this.permission.getPermission());
    }

    public @NotNull String getPermission() {
        return this.permission.getPermission();
    }

    public boolean isValidExecutor(@NotNull Sender sender) {
        return true;
    }

    public abstract int getMinArgumentCount();

    public abstract @NotNull AbstractCommand get(@NotNull List<@NotNull String> args);

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
