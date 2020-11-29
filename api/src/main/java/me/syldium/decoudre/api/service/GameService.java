package me.syldium.decoudre.api.service;

import me.syldium.decoudre.api.arena.DeArena;
import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.player.DePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GameService {

    /**
     * Gets a player's game from his {@link UUID}.
     *
     * @param uuid The player's unique identifier.
     * @return The game, if any.
     */
    @NotNull Optional<@NotNull DeGame> getGame(@NotNull UUID uuid);

    /**
     * Gets a player in game.
     *
     * @param uuid The player's unique identifier.
     * @return The player in game, if any.
     */
    @NotNull Optional<@NotNull DePlayer> getInGamePlayer(@NotNull UUID uuid);

    /**
     * Creates a new place to dive and make dés à coudre.
     *
     * @param name The pool name.
     * @return The new pool, or null if the name was already taken.
     */
    @Nullable DeArena createArena(@NotNull String name);

    /**
     * Gets a {@link DeArena}.
     *
     * @param name The arena name
     * @return The arena, if any.
     */
    @NotNull Optional<@NotNull DeArena> getArena(@NotNull String name);

    /**
     * Gets a set of the registered {@link DeArena}.
     *
     * @return The arenas.
     */
    @NotNull @UnmodifiableView Set<@NotNull DeArena> getArenas();

    /**
     * Removes any reference to an already created arena.
     *
     * @param arena An arena.
     */
    void removeArena(@NotNull DeArena arena);
}
