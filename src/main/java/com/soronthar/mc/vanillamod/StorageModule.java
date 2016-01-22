package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class StorageModule implements Construct{
    BlockPos chestPos;

    public StorageModule(BlockPos chestPos) {
        this.chestPos=chestPos;
    }

    public static StorageModule detectStorage(World world, EngineModule engine, EnumFacing facing) {
        BlockPos controllerPos = engine.controllerPos;

        BlockPos chestPos = controllerPos.offset(facing.getOpposite());

        if (GeneralUtils.isBlockInPos(world, chestPos, Blocks.chest)) {
            return new StorageModule(chestPos);
        } else {
            return null;
        }
    }

    @Override
    public boolean isValidStructure(World world) {
        return GeneralUtils.isBlockInPos(world, this.chestPos, Blocks.chest);
    }

    @Override
    public void move(World world, EnumFacing facing, int step) {
        this.chestPos=MovingMachine.moveBlock(world, this.chestPos, facing, step);
    }

    @Override
    public List<BlockPos> getBlockPosList() {
        return Collections.singletonList(chestPos);
    }
}
