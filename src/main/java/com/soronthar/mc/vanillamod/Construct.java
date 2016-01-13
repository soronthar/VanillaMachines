package com.soronthar.mc.vanillamod;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface Construct {
    boolean isValidStructure(World world);

    void readFromNBT(NBTTagCompound compound);

    void writeToNBT(NBTTagCompound compound);
}
