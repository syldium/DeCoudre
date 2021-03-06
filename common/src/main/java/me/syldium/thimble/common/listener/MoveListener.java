package me.syldium.thimble.common.listener;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.ConfigManager;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class MoveListener<P extends ThimblePlugin> implements Reloadable {

    protected final P plugin;
    protected int maxDistanceSquared;
    protected boolean quitOnTp;

    public MoveListener(@NotNull P plugin) {
        this.plugin = plugin;
        this.reload(plugin.getConfigManager());
    }

    protected final void onMove(@NotNull UUID playerUniqueId, @NotNull BlockVector from, @NotNull BlockVector to) {
        if (from.equals(to)) return;
        Optional<ThimblePlayer> inGamePlayerOpt = this.plugin.getGameService().player(playerUniqueId);
        if (!inGamePlayerOpt.isPresent()) return;

        ThimblePlayer inGamePlayer = inGamePlayerOpt.get();
        if (inGamePlayer.isVanished() || inGamePlayer.isJumping()) return;

        Location loc = inGamePlayer.game().state().isStarted() ?
                inGamePlayer.game().arena().waitLocation()
                : inGamePlayer.game().arena().spawnLocation();
        if (loc == null) return;

        int distanceSquared = (int) loc.horizontalDistanceSquared(to);
        if (distanceSquared > this.maxDistanceSquared) {
            Player player = this.plugin.getPlayer(playerUniqueId);
            if (player != null) {
                player.teleport(loc);
            }
        }
    }

    protected final void removePlayerFromArena(@NotNull UUID playerUniqueId) {
        Optional<ThimbleGame> game = this.getGame(playerUniqueId);
        game.ifPresent(thimbleGame -> thimbleGame.removePlayer(playerUniqueId));
    }

    protected final void removePlayerFromArena(@NotNull UUID playerUniqueId, @NotNull Location location) {
        Optional<ThimbleGame> game = this.getGame(playerUniqueId);
        if (!game.isPresent()) return;

        ThimbleArena arena = game.get().arena();
        for (Location arenaLoc : new Location[]{arena.spawnLocation(), arena.jumpLocation(), arena.waitLocation()}) {
            if (location.distanceSquared(arenaLoc) < 10) {
                return;
            }
        }
        game.get().removePlayer(playerUniqueId);
    }

    private @NotNull Optional<@NotNull ThimbleGame> getGame(@NotNull UUID playerUniqueId) {
        return this.plugin.getGameService().playerGame(playerUniqueId);
    }

    @Override
    public void reload(@NotNull ConfigManager<?> configManager) {
        ConfigNode node = configManager.getMainConfig().getGameNode();
        int maxDistance = node.getInt("max-spectator-distance", 40);
        this.maxDistanceSquared = maxDistance > 0 ? maxDistance * maxDistance : -1;
        this.quitOnTp = node.getBool("leave-arena-when-tp", true);
    }
}
