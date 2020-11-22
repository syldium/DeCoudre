package me.syldium.decoudre.sponge.command;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.AbstractSender;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandSource;

public class SpongeSender extends AbstractSender<CommandSource> {

    public SpongeSender(@NotNull DeCoudrePlugin plugin, @NotNull CommandSource handle, @NotNull Audience audience) {
        super(plugin, handle, audience);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.getHandle().hasPermission(permission);
    }
}
