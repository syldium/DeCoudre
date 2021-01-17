package me.syldium.thimble.common.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.api.util.Leaderboard;
import me.syldium.thimble.common.player.PlayerStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StatsServiceImpl implements StatsService {

    private final Map<Ranking, Leaderboard> leaderboard = new EnumMap<>(Ranking.class);

    private final DataService dataService;
    private final Executor executor;

    public StatsServiceImpl(@NotNull DataService dataService, @NotNull Executor executor) {
        this.dataService = dataService;
        this.executor = executor;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Optional<ThimblePlayerStats>> getPlayerStatistics(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.dataService.getPlayerStatistics(uuid), this.executor);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Optional<@NotNull ThimblePlayerStats>> getPlayerStatistics(@NotNull String name) {
        return CompletableFuture.supplyAsync(() -> this.dataService.getPlayerStatistics(name), this.executor);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Void> savePlayerStatistics(@NotNull ThimblePlayerStats statistics) {
        return CompletableFuture.supplyAsync(() -> {
            this.dataService.savePlayerStatistics(statistics);
            return null;
        }, this.executor);
    }

    @Override
    public @NotNull Leaderboard getLeaderboard(@NotNull Ranking criteria) {
        return this.leaderboard.computeIfAbsent(criteria, this::fetchLeaderboard);
    }

    private @NotNull Leaderboard fetchLeaderboard(@NotNull Ranking criteria) {
        // SqlDataService is not currently thread-safe and the return could be used as a placeholder.
        return CompletableFuture.supplyAsync(() -> this.dataService.getLeaderboard(criteria), this.executor).join();
    }

    /**
     * Updates the cached leaderboards.
     *
     * @param stats The updated statistics.
     */
    public void updateLeaderboard(@NotNull ThimblePlayerStats stats) {
        for (Leaderboard leaderboard : this.leaderboard.values()) {
            leaderboard.add(stats);
        }
    }

    public void updateLeaderboard(@NotNull ThimblePlayer player) {
        this.updateLeaderboard(new PlayerStats(
                player.uuid(),
                player.name(),
                player.wins(),
                player.losses(),
                player.jumps(),
                player.failedJumps(),
                player.thimbles()
        ));
    }
}
