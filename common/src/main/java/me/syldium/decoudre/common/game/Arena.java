package me.syldium.decoudre.common.game;

import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.api.arena.DeArena;
import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.common.DeCoudrePlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Arena implements DeArena {

    private final String name;
    private Location spawnLocation;
    private Location jumpLocation;
    private int minPlayers = 1;
    private int maxPlayers = 8;

    private Game game;
    private final DeCoudrePlugin plugin;

    public Arena(@NotNull DeCoudrePlugin plugin, @NotNull String name) {
        this.plugin = plugin;
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull Location getSpawnLocation() {
        return this.spawnLocation;
    }

    @Override
    public void setSpawnLocation(@NotNull Location location) {
        this.spawnLocation = location;
    }

    @Override
    public @NotNull Location getJumpLocation() {
        return this.jumpLocation;
    }

    @Override
    public void setJumpLocation(@NotNull Location location) {
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
    public @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull UUID player) {
        if (this.game == null) {
            this.game = new Game(this.plugin, this);
        }
        return this.game.addPlayer(player);
    }

    @Override
    public boolean removePlayer(@NotNull UUID player) {
        return this.game.removePlayer(player);
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
