package me.syldium.thimble.game;

import me.syldium.thimble.PluginMock;
import me.syldium.thimble.mock.player.PlayerMock;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.common.game.Arena;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    private final PluginMock plugin;

    public GameTest() throws IOException {
        this.plugin = new PluginMock();
    }

    @AfterEach
    public void cancelTasks() {
        this.plugin.getScheduler().cancelAllTasks();
    }

    @Test
    public void start() {
        Arena arena = this.newArena(this.plugin);
        assertTrue(arena.getGame().isEmpty(), "No Game instance should exist.");
        PlayerMock player = this.plugin.addPlayer();
        assertNotEquals(arena.getSpawnLocation(), player.getLocation());
        arena.addPlayer(player);
        assertTrue(arena.getGame().isPresent(), "A game object should be created.");
        assertTrue(this.plugin.getGameService().getGame(player).isPresent());
        this.plugin.getScheduler().assertScheduled();
        this.plugin.getScheduler().nextTick();
        assertEquals(ThimbleState.WAITING, arena.getGame().get().getState());
        assertEquals(arena.getSpawnLocation(), player.getLocation());
    }

    @Test
    public void starting() {
        Arena arena = this.newArena(this.plugin);
        arena.addPlayer(this.plugin.addPlayer());
        arena.addPlayer(this.plugin.addPlayer());
        assertEquals(ThimbleState.WAITING, arena.getGame().get().getState());
        this.plugin.getScheduler().nextTick();
        assertEquals(ThimbleState.STARTING, arena.getGame().get().getState());
        this.plugin.getScheduler().nextTicks(Ticks.TICKS_PER_SECOND + 1);
        assertEquals(ThimbleState.PLAYING, arena.getGame().get().getState());
    }

    private @NotNull Arena newArena(@NotNull PluginMock plugin) {
        Arena arena = new Arena(plugin, "test");
        UUID world = UUID.randomUUID();
        arena.setMinPlayers(2).setMaxPlayers(4)
            .setSpawnLocation(new Location(world, 100, 70, 100))
            .setWaitLocation(new Location(world, 100, 70, 150))
            .setJumpLocation(new Location(world, 100, 110, 150))
            .setPoolMinPoint(null).setPoolMaxPoint(null);
        return arena;
    }
}
