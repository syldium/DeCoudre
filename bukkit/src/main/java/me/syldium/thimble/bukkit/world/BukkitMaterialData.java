package me.syldium.thimble.bukkit.world;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("deprecation")
class BukkitMaterialData implements BukkitBlockData {

    private static final Method SET_DATA;

    static {
        Method setData = null;
        try {
            // noinspection JavaReflectionMemberAccess
            setData = Block.class.getMethod("setData", byte.class);
        } catch (NoSuchMethodException ignored) { }
        SET_DATA = setData;
    }

    final MaterialData handle;

    BukkitMaterialData(@NotNull Material material) {
        this.handle = new MaterialData(material);
    }

    BukkitMaterialData(@NotNull MaterialData handle) {
        this.handle = handle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitMaterialData that = (BukkitMaterialData) o;
        return this.handle.equals(that.handle);
    }

    @Override
    public int hashCode() {
        return this.handle.hashCode();
    }

    @Override
    public @NotNull Material material() {
        return this.handle.getItemType();
    }

    @Override
    public @NotNull ItemStack itemStack() {
        return this.handle.toItemStack(1);
    }

    @Override
    public void setBlock(@NotNull Block block) {
        block.setType(this.handle.getItemType());
        try {
            SET_DATA.invoke(block, this.handle.getData());
        } catch (IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isSimilar(@NotNull ItemStack itemStack) {
        return this.handle.equals(itemStack.getData());
    }
}
