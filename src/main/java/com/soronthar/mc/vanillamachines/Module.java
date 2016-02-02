package com.soronthar.mc.vanillamachines;

import net.minecraft.util.BlockPos;

import java.util.List;

public interface Module {
    boolean isValidStructure();

    void move(int step);

    List<BlockPos> getBlockPosList();

    void setMachine(MovingMachine machine);
}
