package me.syldium.thimble.common.listener;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.ConfigManager;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.config.SavedPlayer;
import me.syldium.thimble.common.game.Game;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public abstract class ConnectionListener<Plugin extends ThimblePlugin, Player> implements Reloadable {

    protected final Plugin plugin;
    protected boolean quitOnDisconnect;
    protected boolean inventoryCleared;

    public ConnectionListener(@NotNull Plugin plugin) {
        this.plugin = plugin;
        int savesSize = plugin.getSavedPlayersManager().getPending().size();
        if (savesSize > 0) {
            plugin.getLogger().info("Found " + savesSize + " player save(s).");
        }
        this.reload(this.plugin.getConfigManager());
    }

    @SuppressWarnings("unchecked")
    public final void onJoin(@NotNull UUID playerUniqueId) {
        Optional<ThimblePlayer> inGamePlayerOpt = this.plugin.getGameService().player(playerUniqueId);
        if (inGamePlayerOpt.isPresent()) {
            ((Game) inGamePlayerOpt.get().game()).spectate(inGamePlayerOpt.get());
            return;
        }

        if (!this.plugin.getSavedPlayersManager().getPending().contains(playerUniqueId)) {
            return;
        }

        this.plugin.getSavedPlayersManager().getInventorySave(playerUniqueId).thenAccept(optional ->
                optional.ifPresent(saved -> this.plugin.runSync(() -> this.onSavedPlayerFound(playerUniqueId, (SavedPlayer<Player>) saved)))
        );
    }

    public final void onQuit(@NotNull UUID playerUniqueId) {
        Optional<ThimbleGame> optional = this.plugin.getGameService().playerGame(playerUniqueId);
        if (!optional.isPresent()) return;
        ThimbleGame game = optional.get();

        if (this.quitOnDisconnect || game.state().isNotStarted()) {
            game.removePlayer(playerUniqueId, false);
        }
    }

    @Override
    public void reload(@NotNull ConfigManager<?> configManager) {
        ConfigNode node = configManager.getMainConfig().getGameNode();
        this.quitOnDisconnect = node.getBool("quit-game-on-disconnect", false);
        this.inventoryCleared = node.getBool("clear-inventory", true);
    }

    protected abstract void onSavedPlayerFound(@NotNull UUID playerUniqueId, @NotNull SavedPlayer<Player> savedPlayer);
}
