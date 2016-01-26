package com.soronthar.mc.vanillamod;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by pc on 25/01/2016.
 */
public interface Storage extends Module {
    void addToStorage(World world, List<ItemStack> drops);
}
