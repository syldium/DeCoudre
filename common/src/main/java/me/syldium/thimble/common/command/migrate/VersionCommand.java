package me.syldium.thimble.common.command.migrate;

import me.syldium.thimble.api.Thimble;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Argument;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.update.GitHubReleaseInfo;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class VersionCommand extends ChildCommand.One<String> {

    private static final Template VERSION = Template.of("version", Thimble.pluginVersion().asComponent());
    private static final Template RELEASES_URL = Template.of("releases", "https://github.com/syldium/Thimble/releases");

    public VersionCommand() {
        super("version", new UpdateArgument().optional(), null, Permission.migrate());
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @Nullable String arg) throws CommandException {
        boolean update = arg != null && arg.equals("update");
        if (!update) {
            sender.sendFeedback(CommandResult.info(
                    MessageKey.FEEDBACK_VERSION_CURRENT,
                    VERSION
            ));
        }

        if (update && plugin.getUpdateChecker().isUpToDate()) {
            return CommandResult.success(MessageKey.FEEDBACK_VERSION_ALREADY_LATEST);
        }

        plugin.getUpdateChecker().getReleaseInfo().thenAccept(releaseInfo -> {
            sender.sendFeedback(this.execute(plugin, sender, releaseInfo, update));
        });
        return CommandResult.success();
    }

    private @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @Nullable GitHubReleaseInfo releaseInfo, boolean update) {
        if (releaseInfo == null) {
            return CommandResult.error(MessageKey.FEEDBACK_VERSION_UNKNOWN_LATEST, RELEASES_URL, VERSION);
        }

        if (plugin.getUpdateChecker().isUpToDate() || releaseInfo.latestPlatformAsset() == null) {
            return CommandResult.success(MessageKey.FEEDBACK_VERSION_UP_TO_DATE, VERSION);
        }

        if (update) {
            sender.sendFeedback(CommandResult.success(MessageKey.FEEDBACK_VERSION_DOWNLOADING));
            if (plugin.updatePlugin(releaseInfo.latestPlatformAsset())) {
                return CommandResult.success(MessageKey.FEEDBACK_VERSION_FINISHED);
            } else {
                return CommandResult.error(MessageKey.FEEDBACK_VERSION_FAILED);
            }
        }

        return CommandResult.error(MessageKey.FEEDBACK_VERSION_OUTDATED, VERSION, Template.of("latest", releaseInfo.asComponent()));
    }

    private static class UpdateArgument extends Argument<String> {

        UpdateArgument() {
            super("update");
        }

        @Override
        public @NotNull String parse(@NotNull ThimblePlugin plugin, @NotNull String given) throws CommandException {
            return given;
        }

        @Override
        public List<String> tabComplete(@NotNull ThimblePlugin plugin, @NotNull String given, @NotNull Sender sender) {
            if ("update".startsWith(given)) {
                return Collections.singletonList("update");
            }
            return Collections.emptyList();
        }
    }
}