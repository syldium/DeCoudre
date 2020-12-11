package me.syldium.decoudre.common.game;

import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.api.BlockVector;
import me.syldium.decoudre.api.arena.DeArena;
import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.arena.DeGameMode;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.player.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Arena implements DeArena {

    private final String name;
    private Location spawnLocation;
    private Location jumpLocation;
    private BlockVector minimumPoint;
    private BlockVector maximumPoint;
    private int minPlayers = 1;
    private int maxPlayers = 8;

    private Game game;
    private final DeCoudrePlugin plugin;
    private DeGameMode gameMode = DeGameMode.SINGLE;

    public Arena(@NotNull DeCoudrePlugin plugin, @NotNull String name) {
        this.plugin = plugin;
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @Nullable Location getSpawnLocation() {
        return this.spawnLocation;
    }

    @Override
    public void setSpawnLocation(@NotNull Location location) {
        if (this.game != null) {
            Objects.requireNonNull(location, "Cannot unset the spawn location on a game that has already been started.");
        }
        this.spawnLocation = location;
    }

    @Override
    public @Nullable Location getJumpLocation() {
        return this.jumpLocation;
    }

    @Override
    public void setJumpLocation(@NotNull Location location) {
        if (this.game != null) {
            Objects.requireNonNull(location, "Cannot unset the jump location on a game that has already been started.");
        }
        this.jumpLocation = location;
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getMinPlayers() {
        return this.minPlayers;
    }

    @Override
    public void setMinPlayers(int minimum) throws IllegalArgumentException {
        if (minimum < 1 || minimum > this.maxPlayers) {
            throw new IllegalArgumentException();
        }
        this.minPlayers = minimum;
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public void setMaxPlayers(int maximum) throws IllegalArgumentException {
        if (maximum < 1 || maximum < this.minPlayers) {
            throw new IllegalArgumentException();
        }
        this.maxPlayers = maximum;
    }

    @Override
    public @NotNull Optional<@NotNull DeGame> getGame() {
        return Optional.ofNullable(this.game);
    }

    @Override
    public @NotNull DeGameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public void setGameMode(@NotNull DeGameMode gameMode) {
        this.gameMode = Objects.requireNonNull(gameMode, "The game mode cannot be null.");
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull UUID uuid) {
        this.initGame();
        return this.game.addPlayer(uuid);
    }

    public @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull Player player) {
        this.initGame();
        return this.game.addPlayer(player);
    }

    private void initGame() {
        if (this.game == null) {
            if (!this.isSetup()) {
                throw new IllegalStateException(String.format("The %s arena is not correctly configured.", this.name));
            }

            if (this.gameMode == DeGameMode.CONCURRENT) {
                this.game = new ConcurrentGame(this.plugin, this);
            } else {
                this.game = new SingleGame(this.plugin, this);
            }
        }
    }

    @Override
    public boolean removePlayer(@NotNull UUID player) {
        return this.game.removePlayer(player);
    }

    @Override
    public @Nullable BlockVector getPoolMinPoint() {
        return this.minimumPoint;
    }

    @Override
    public void setPoolMinPoint(@NotNull BlockVector point) {
        this.minimumPoint = point;
    }

    @Override
    public @Nullable BlockVector getPoolMaxPoint() {
        return this.maximumPoint;
    }

    @Override
    public void setPoolMaxPoint(@NotNull BlockVector point) {
        this.maximumPoint = point;
    }

    @Override
    public @NotNull Component asComponent() {
        return Component.text(this.name);
    }

    void checkGame() {
        if (this.game != null && this.game.isEmpty()) {
            this.game.cancel();
            this.game = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return this.name.equals(arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "Arena{" +
                "name='" + this.name + '\'' +
                ", minPlayers=" + this.minPlayers +
                ", maxPlayers=" + this.maxPlayers +
                ", hasGame=" + (this.game != null) +
                '}';
    }
}
