package me.syldium.decoudre.bukkit;

import me.syldium.decoudre.bukkit.adapter.BukkitPlayerAdapter;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.config.ArenaConfig;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.util.Task;
import me.syldium.decoudre.bukkit.config.BukkitArenaConfig;
import me.syldium.decoudre.bukkit.config.BukkitMainConfig;
import me.syldium.decoudre.bukkit.util.BukkitTask;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

public class DeBukkitPlugin extends DeCoudrePlugin {

    private final DeCoudreBootstrap bootstrap;
    private final BukkitAudiences audiences;
    private final BukkitPlayerAdapter platform;
    private final BukkitArenaConfig arenaConfig;

    public DeBukkitPlugin(@NotNull DeCoudreBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.audiences = BukkitAudiences.create(bootstrap);
        this.arenaConfig = new BukkitArenaConfig(this, this.getFile("arenas.yml"));
        this.platform = new BukkitPlayerAdapter(bootstrap, this.audiences);
        this.enable(new BukkitMainConfig(this, this.getFile("config.yml")));
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.bootstrap.getLogger();
    }

    @Override
    public @NotNull ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.bootstrap.getDataFolder();
    }

    @Override
    public @NotNull Task startGameTask(@NotNull Runnable runnable) {
        return new BukkitTask(this.bootstrap.getServer().getScheduler().scheduleSyncRepeatingTask(this.bootstrap, runnable, 0L, 1L));
    }

    @Override
    public @Nullable Player getPlayer(@NotNull UUID uuid) {
        org.bukkit.entity.Player player = this.bootstrap.getServer().getPlayer(uuid);
        if (player == null) {
            return null;
        }
        return this.platform.asAbstractPlayer(player);
    }

    @Override
    public @NotNull BukkitPlayerAdapter getPlayerAdapter() {
        return this.platform;
    }

    public void registerEvents(@NotNull Listener listener) {
        this.bootstrap.getServer().getPluginManager().registerEvents(listener, this.bootstrap);
    }
}
