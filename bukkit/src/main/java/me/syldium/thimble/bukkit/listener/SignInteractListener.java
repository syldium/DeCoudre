package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.bukkit.BukkitAdapter;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.util.SignAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

import static me.syldium.thimble.bukkit.adapter.BukkitPlayer.isVanished;

public class SignInteractListener implements Listener {

    private final ThBukkitPlugin plugin;
    private final Set<Material> clickable;

    public SignInteractListener(@NotNull ThBukkitPlugin plugin, @NotNull Set<@NotNull Material> clickable) {
        this.plugin = plugin;
        this.clickable = clickable;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!this.clickable.contains(event.getClickedBlock().getType())) return;

        if (!event.getPlayer().hasPermission("thimble.sign.use")) return;

        Block block = event.getClickedBlock();
        BlockPos position = BukkitAdapter.get().asAbstract(block);

        Optional<ThimbleArena> arena = this.plugin.getGameService().arenaFromSign(position);
        if (arena.isPresent()) {
            event.setCancelled(true);

            if (this.plugin.getGameService().playerGame(event.getPlayer().getUniqueId()).isPresent()) {
                this.plugin.sendFeedback(event.getPlayer(), CommandResult.error(MessageKey.FEEDBACK_GAME_ALREADY_IN_GAME));
                return;
            }

            Optional<ThimbleGame> game = arena.get().game();
            if (game.isPresent()) {
                if (game.get().state().isStarted()) {
                    this.plugin.sendFeedback(event.getPlayer(), CommandResult.error(MessageKey.FEEDBACK_GAME_STARTED_GAME));
                    return;
                }
                if (!game.get().acceptPlayer() && !isVanished(event.getPlayer())) {
                    this.plugin.sendFeedback(event.getPlayer(), CommandResult.error(MessageKey.FEEDBACK_GAME_FULL));
                    return;
                }
            }

            try {
                arena.get().addPlayer(event.getPlayer().getUniqueId());
                this.plugin.sendFeedback(event.getPlayer(), CommandResult.success(MessageKey.FEEDBACK_GAME_JOINED));
            } catch (IllegalStateException ex) {
                this.plugin.sendFeedback(event.getPlayer(), CommandResult.error(MessageKey.FEEDBACK_ARENA_NOT_CONFIGURED));
            }
            return;
        }

        Optional<SignAction> action = this.plugin.getGameService().getActionFromSign(position);
        if (action.isPresent()) {
            event.setCancelled(true);
            action.get().run(this.plugin, this.plugin.getPlayerAdapter().asAbstractPlayer(event.getPlayer()));
        }
    }
}
