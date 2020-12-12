package me.syldium.thimble.common.game;

import me.syldium.thimble.api.arena.ThimbleSingleGame;
import me.syldium.thimble.api.arena.ThimbleGameState;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.world.PoolBlock;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;

public class SingleGame extends Game implements ThimbleSingleGame {

    private final Queue<UUID> queue = new ArrayDeque<>();
    private UUID jumper;

    protected final int jumpTicks;

    public SingleGame(@NotNull ThimblePlugin plugin, @NotNull Arena arena) {
        super(plugin, arena);
        this.jumpTicks = plugin.getMainConfig().getJumpTimeSingleMode() * Ticks.TICKS_PER_SECOND;
    }

    @Override
    protected void onCountdownEnd() {
        for (InGamePlayer player : this.players) {
            if (!player.isSpectator()) {
                this.queue.add(player.uuid());
            }
        }
    }

    @Override
    protected void onTimerEnd() {

    }

    @Override
    protected void tickGame() {
        if (this.jumper == null) {
            this.timer = this.plugin.getMainConfig().getJumpTimeSingleMode() * Ticks.TICKS_PER_SECOND;
            if (this.queue.isEmpty()) {
                this.state = ThimbleGameState.END;
                return;
            }
            Player player = this.plugin.getPlayer(this.queue.poll());
            if (player != null) {
                player.teleport(this.arena.getJumpLocation());
                this.jumper = player.uuid();
            }
            return;
        }

        Player jumper = this.plugin.getPlayer(this.jumper);
        if (jumper == null) {
            this.onJump(null, this.players.get(this.jumper), JumpVerdict.MISSED);
            return;
        }

        this.jumperMedia.progress(jumper, this.timer, this.jumpTicks);
        if (jumper.isInWater()) {
            PoolBlock block = jumper.getFirstLiquidBlock();
            block.setBlockData(this.players.get(jumper).getChosenBlock());
            this.blocks.add(block);
            JumpVerdict verdict = this.plugin.getPlayerAdapter().isDeCoudre(block) ? JumpVerdict.THIMBLE : JumpVerdict.LANDED;
            this.onJump(jumper, this.players.get(jumper), verdict);

            if (this.remainingWaterBlocks.remove(block.getPosition()) && this.remainingWaterBlocks.isEmpty()) {
                this.end(this.getFirstPlayer());
            }
        }

        if (this.timer < 1) {
            this.onJump(jumper, this.players.get(jumper), JumpVerdict.MISSED);
        }
    }

    @Override
    protected void onJump(@Nullable Player player, @NotNull InGamePlayer inGamePlayer, @NotNull JumpVerdict verdict) {
        if (verdict == JumpVerdict.MISSED) {
            inGamePlayer.decrementLifes();
        } else {
            inGamePlayer.incrementJumps();
            if (verdict == JumpVerdict.THIMBLE) {
                inGamePlayer.incrementLifes();
                inGamePlayer.incrementThimbles();
            }
        }

        if (player != null) {
            this.jumperMedia.hide(player);
            this.sendJumpMessage(player, inGamePlayer, verdict);
        }

        this.jumper = null;
        if (inGamePlayer.getPoints() > 0) {
            this.queue.offer(inGamePlayer.uuid());
        } else if (this.queue.size() < 2) {
            this.end(inGamePlayer);
        }
    }

    @Override
    public @Nullable UUID getCurrentJumper() {
        return this.jumper;
    }

    @Override
    public @Nullable UUID peekNextJumper() {
        return this.queue.peek();
    }

    @Override
    public boolean isJumping(@NotNull UUID playerUUID) {
        return this.jumper != null && this.jumper.equals(playerUUID);
    }

    @Override
    public boolean removePlayer(@NotNull UUID player) {
        boolean removed = super.removePlayer(player);
        if (removed && Objects.equals(this.jumper, player)) {
            this.jumper = null;
            Player p = this.plugin.getPlayer(player);
            if (p != null) {
                this.jumperMedia.hide(p);
            }
        }
        return removed;
    }

    @Override
    public String toString() {
        return "SingleGame{" +
                "arena=" + this.arena +
                ", state=" + this.state +
                ", players=" + this.players.values() +
                ", remainingWaterBlocks=" + this.remainingWaterBlocks.size() +
                ", timer=" + this.timer +
                ", queue=" + this.queue +
                ", jumper=" + this.jumper +
                '}';
    }
}
