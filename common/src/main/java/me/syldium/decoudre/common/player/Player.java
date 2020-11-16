package me.syldium.decoudre.common.player;

import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.world.PoolBlock;
import net.kyori.adventure.identity.Identified;
import org.jetbrains.annotations.NotNull;

public interface Player extends Identified, Sender {

    /**
     * Gets the player's current position.
     *
     * @return A new copy of Location containing the position of this player
     */
    @NotNull Location getLocation();

    /**
     * Teleports this entity to the given location.
     *
     * @param location New location to teleport this player to.
     * @return {@code true} if the teleport was successful.
     */
    boolean teleport(@NotNull Location location);

    /**
     * Gets on which blocks does the player standing on.
     *
     * @return The blocks below the bounding box.
     */
    @NotNull PoolBlock[] getBlocksBelow();

    /**
     * Gets the surface block of a water/lava column.
     *
     * <p>If the player is not {@link #isInWater()}, the returned block will not be a liquid.</p>
     *
     * @return The first liquid block.
     */
    @NotNull PoolBlock getFirstLiquidBlock();

    /**
     * Check if the player is in water.
     *
     * @return Whether the player is in water.
     */
    boolean isInWater();
}
