package me.syldium.thimble.common.player;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.common.game.Game;
import me.syldium.thimble.common.world.BlockData;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public class InGamePlayer extends PlayerStats implements ThimblePlayer {

    private boolean spectator = false;
    private final boolean vanished;
    private final Game game;
    private final Location lastLocation;
    private BlockData block;
    private int points = 1;
    private int jumps = 0;

    public InGamePlayer(@NotNull Player player, @NotNull ThimblePlayerStats stats, @NotNull BlockData block, @NotNull Game game) {
        super(stats.uuid(), stats.name(), stats.wins(), stats.losses(), stats.jumps(), stats.failedJumps(), stats.thimbles());
        this.block = block;
        this.game = game;
        this.lastLocation = player.getLocation();
        this.vanished = player.isVanished();
    }

    public InGamePlayer(@NotNull Player player, @NotNull BlockData block, @NotNull Game game) {
        super(player.uuid(), player.name());
        this.block = block;
        this.game = game;
        this.lastLocation = player.getLocation();
        this.vanished = player.isVanished();
    }

    @TestOnly
    public InGamePlayer(@NotNull UUID uuid, @NotNull String name, @NotNull BlockData block, @NotNull Game game) {
        super(uuid, name);
        this.block = block;
        this.game = game;
        this.lastLocation = new Location(new WorldKey(UUID.randomUUID().toString().substring(0, 16)), 0, 0, 0);
        this.vanished = false;
    }

    @Override
    public int points() {
        return this.points;
    }

    @Override
    public int jumpsForGame() {
        return this.jumps;
    }

    @Override
    public boolean isSpectator() {
        return this.spectator;
    }

    @Override
    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    @Override
    public boolean isVanished() {
        return this.vanished;
    }

    @Override
    public boolean isJumping() {
        return this.game.isJumping(this.uuid());
    }

    @Override
    public @NotNull Game getGame() {
        return this.game;
    }

    @Override
    public void incrementJumps() {
        super.incrementJumps();
        this.jumps++;
    }

    public void incrementPoints() {
        this.points++;
    }

    public void incrementPoints(int points) {
        this.points += points;
    }

    public void decrementPoints() {
        if (--this.points < 1) {
            this.spectator = true;
        }
    }

    public @NotNull BlockData getChosenBlock() {
        return this.block;
    }

    public void setChosenBlock(@NotNull BlockData block) {
        this.block = block;
    }

    public @NotNull Location getLastLocation() {
        return this.lastLocation;
    }
}
