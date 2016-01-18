package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SmallDrillModule implements Construct {
    BlockPos drillHeadPos;

    public SmallDrillModule(BlockPos drillHeadPos) {
        this.drillHeadPos = drillHeadPos;
    }

    public static SmallDrillModule detect(World world, BlockPos controllerPos, EnumFacing facing) {
        BlockPos drillHeadPos = controllerPos.offset(facing);
        if (GeneralUtils.isBlockInPos(world, drillHeadPos, Blocks.iron_block)) {
            return new SmallDrillModule(drillHeadPos);
        } else {
            return null;
        }
    }

    @Override
    public boolean isValidStructure(World world) {
        return GeneralUtils.isBlockInPos(world, this.drillHeadPos, Blocks.iron_block); //TODO: encapsulate the iron_block thingie
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag("smallDrillModule");
        this.drillHeadPos = GeneralUtils.readBlockPosFromNBT(tag, "drillHeadPos");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        GeneralUtils.writeBlockPosToNBT(tag, "drillHeadPos", this.drillHeadPos);
        compound.setTag("smallDrillModule",tag);
    }

    @Override
    public boolean canMove(World world, EnumFacing facing, int step) {
        return GeneralUtils.canBlockBeReplaced(world, this.drillHeadPos.offset(facing, step));
    }

    @Override
    public void powerOff(World world) {

    }

    @Override
    public void move(World world, EnumFacing facing, int step) {
        this.drillHeadPos = PoweredConstruct.moveBlock(world, this.drillHeadPos, facing, step);
    }

}
