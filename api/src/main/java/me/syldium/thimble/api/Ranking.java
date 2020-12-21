package me.syldium.thimble.api;

import me.syldium.thimble.api.player.ThimblePlayerStats;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Various ranking criteria.
 */
public enum Ranking {

    /**
     * Depending on the number of victories.
     */
    WINS(ThimblePlayerStats::getWins),

    /**
     * Based on the number of defeats.
     */
    LOSSES(ThimblePlayerStats::getLosses),

    /**
     * Per the number of successful jumps.
     */
    JUMPS(ThimblePlayerStats::getJumps),

    /**
     * Per the number of failed jumps.
     */
    FAILS(ThimblePlayerStats::getFailedJumps),

    /**
     * According to the number of thimbles.
     */
    THIMBLES(ThimblePlayerStats::getThimbles);

    private final Function<ThimblePlayerStats, Integer> getter;

    Ranking(@NotNull Function<ThimblePlayerStats, Integer> getter) {
        this.getter = getter;
    }

    /**
     * Returns the player's score using the criteria.
     *
     * @param stats An object of statistics.
     * @return A numeric value.
     */
    public int get(@NotNull ThimblePlayerStats stats) {
        return this.getter.apply(stats);
    }

    public @NotNull Function<ThimblePlayerStats, Integer> getter() {
        return this.getter;
    }
}
