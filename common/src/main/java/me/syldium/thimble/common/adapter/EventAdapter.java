package me.syldium.thimble.common.adapter;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EventAdapter<P> {

    boolean callPlayerJoinArenaEvent(@NotNull ThimbleGame game, @NotNull P player);

    boolean callGameChangeState(@NotNull ThimbleGame game, @NotNull ThimbleState newState);

    @NotNull JumpVerdict callJumpVerdictEvent(@NotNull ThimblePlayer player, @NotNull JumpVerdict verdict);

    void callGameEndEvent(@NotNull ThimbleGame game, @Nullable ThimblePlayer player, boolean isSolo);

    void callGameAbortedEvent(@NotNull ThimbleGame game, boolean startAborted, boolean willBeEmpty);

    @SuppressWarnings("unchecked")
    default boolean callPlayerJoinArenaEvent(@NotNull ThimbleGame arena, @NotNull Player player) {
        return this.callPlayerJoinArenaEvent(arena, ((AbstractPlayer<P>) player).getHandle());
    }
}
