package com.soronthar.mc.vanillamod;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

public interface Module {
    boolean isValidStructure();
//TODO: move should not require the facing
    void move(EnumFacing facing, int step);

    List<BlockPos> getBlockPosList();

    void setMachine(MovingMachine machine);
}
