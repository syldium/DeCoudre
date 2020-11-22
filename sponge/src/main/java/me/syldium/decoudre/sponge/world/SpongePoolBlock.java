package me.syldium.decoudre.sponge.world;

import me.syldium.decoudre.common.world.BlockData;
import me.syldium.decoudre.common.world.Blocks;
import me.syldium.decoudre.common.world.PoolBlock;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class SpongePoolBlock implements PoolBlock {

    private final PassableProperty PASSABLE_PROPERTY = new PassableProperty(true, Property.Operator.EQUAL);

    private final BlockState handle;
    private final Location<World> location;

    public SpongePoolBlock(@NotNull BlockState handle, @NotNull Location<@NotNull World> location) {
        this.handle = handle;
        this.location = location;
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        if (Blocks.WATER.equals(blockData)) {
            this.location.setBlockType(BlockTypes.WATER);
            return;
        }
        this.location.setBlock(((SpongeBlockData) blockData).handle);
    }

    @Override
    public @NotNull BlockData getBlockData() {
        return new SpongeBlockData(this.handle);
    }

    @Override
    public boolean isPassable() {
        Optional<PassableProperty> passable = this.handle.getProperty(PassableProperty.class);
        return passable.isPresent() && this.PASSABLE_PROPERTY.matches(passable.get());
    }

    public @NotNull Location<@NotNull World> getLocation() {
        return this.location;
    }
}
