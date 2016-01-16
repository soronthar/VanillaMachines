package com.soronthar.mc.vanillamod;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface Construct {
    boolean isValidStructure(World world);

    void readFromNBT(NBTTagCompound compound);

    void writeToNBT(NBTTagCompound compound);

    boolean canMove(World world, EnumFacing facing, int step);

    void powerOff(World world);
}
