package me.syldium.decoudre.common.service;

import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.player.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface MessageService {

    /**
     * Gets the translated string from a {@link MessageKey}, and formats it.
     *
     * @param key The message key.
     * @param templates Some placeholders.
     * @return The formatted message component.
     */
    default @NotNull Component formatMessage(@NotNull MessageKey key, @NotNull Template... templates) {
        return this.formatMessage(key, null, templates);
    }

    default @NotNull Component formatMessage(@NotNull CommandResult feedback) {
        Objects.requireNonNull(feedback.getMessageKey(), "Message key");
        return this.formatMessage(feedback.getMessageKey(), feedback.getTextColor(), feedback.getTemplates());
    }

    /**
     * Gets the translated string from a {@link MessageKey}, and formats it.
     *
     * @param key The message key.
     * @param color A default color.
     * @param templates Some placeholders.
     * @return The formatted message component.
     */
    @NotNull Component formatMessage(@NotNull MessageKey key, @Nullable TextColor color, @NotNull Template... templates);
}
