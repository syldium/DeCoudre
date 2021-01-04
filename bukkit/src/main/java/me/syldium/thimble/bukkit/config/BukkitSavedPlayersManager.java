package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.SavedPlayersManager;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BukkitSavedPlayersManager extends SavedPlayersManager<Player> {

    public BukkitSavedPlayersManager(@NotNull ThimblePlugin plugin) {
        super(plugin);
    }

    @Override
    protected @Nullable BukkitSavedPlayer load(@NotNull File file) {
        try (BukkitObjectInputStream data = new BukkitObjectInputStream(new FileInputStream(file))) {
            return (BukkitSavedPlayer) data.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected @NotNull BukkitSavedPlayer create(@NotNull Player player) {
        return new BukkitSavedPlayer(player);
    }
}
