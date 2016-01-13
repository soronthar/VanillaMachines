package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

class EngineModule implements Construct{
    BlockPos activatorPos;
    BlockPos controllerPos;
    BlockPos propellerPos;

    public EngineModule() {
    }

    public EngineModule(BlockPos activatorPos, BlockPos controllerPos, BlockPos propellerPos) {
        this.activatorPos = activatorPos;
        this.controllerPos = controllerPos;
        this.propellerPos = propellerPos;
    }

    @Override
    public boolean isValidStructure(World world) {
        return GeneralUtils.isBlockInPos(world,this.propellerPos,getPropellerBlock())
                && GeneralUtils.isBlockInPos(world,this.activatorPos,getActivatorBlock())
                && GeneralUtils.isBlockInPos(world,this.controllerPos,getControllerBlock());
    }

    @Override
    public boolean canMove(World world, EnumFacing facing, int step) {
        return GeneralUtils.canBlockBeReplaced(world,this.propellerPos.offset(facing,step))
                && GeneralUtils.canBlockBeReplaced(world, this.activatorPos.offset(facing, step))
                && GeneralUtils.canBlockBeReplaced(world,this.controllerPos.offset(facing,step));
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag("engineModule");
        this.activatorPos =GeneralUtils.readBlockPosFromNBT(tag, "activatorPos");
        this.controllerPos =GeneralUtils.readBlockPosFromNBT(tag, "controllerPos");
        this.propellerPos =GeneralUtils.readBlockPosFromNBT(tag, "propellerPos");
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        GeneralUtils.writeBlockPosToNBT(tagCompound, "activatorPos", activatorPos);
        GeneralUtils.writeBlockPosToNBT(tagCompound, "controllerPos", controllerPos);
        GeneralUtils.writeBlockPosToNBT(tagCompound, "propellerPos", propellerPos);
        compound.setTag("engineModule",tagCompound);
    }


    public static Block getActivatorBlock() {
        return Blocks.lever;
    }

    public static Block getPropellerBlock() {
        return Blocks.furnace;
    }

    public static Block getControllerBlock() {
        return Blocks.noteblock;
    }


}
