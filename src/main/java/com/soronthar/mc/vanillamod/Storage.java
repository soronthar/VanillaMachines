package com.soronthar.mc.vanillamod;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public interface Storage extends Module {
    /**
     *
     * @param drops
     * @return True if all the itemstack could be placed in the inventory
     */
    boolean addToStorage(List<ItemStack> drops);


    /**
     *
     * @param drops
     * @return True if the itemstack could be placed in the inventory
     */
    boolean addToStorage(ItemStack itemstack);
}
