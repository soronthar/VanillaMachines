package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

class EngineModule implements Construct {
    BlockPos activatorPos;
    BlockPos controllerPos;
    BlockPos propellerPos;

    int burnTimeLeft=0;

    public EngineModule() {
    }

    private EngineModule(BlockPos activatorPos, BlockPos controllerPos, BlockPos propellerPos) {
        this.activatorPos = activatorPos;
        this.controllerPos = controllerPos;
        this.propellerPos = propellerPos;
    }

    public static EngineModule detectEngineModule(World world, BlockPos activatorPos) {
        EngineModule engine=null;

        EnumFacing controllerFacing = GeneralUtils.findBlockAround(world, activatorPos, getControllerBlock());
        if (controllerFacing != null) {
            BlockPos controllerPos = activatorPos.offset(controllerFacing);
            BlockPos propellerPos = controllerPos.down();
            if (GeneralUtils.isBlockInPos(world, propellerPos, getPropellerBlockOff())) {
                engine=new EngineModule(activatorPos, controllerPos, propellerPos);
            }
        }
        return engine;
    }

    public void burnFuel(World world) {
        if (burnTimeLeft>0) {
            burnTimeLeft--;
        }

        if (burnTimeLeft==0) {
            TileEntityFurnace furnace = (TileEntityFurnace) world.getTileEntity(propellerPos);
            if (furnace!=null
            && furnace.getStackInSlot(1) != null
                    && furnace.getStackInSlot(1).stackSize>0
                    ) {
                ItemStack itemStack = furnace.decrStackSize(1, 1);
                burnTimeLeft+=TileEntityFurnace.getItemBurnTime(itemStack);
            }
        }
    }

    @Override
    public boolean isValidStructure(World world) {
        return (GeneralUtils.isBlockInPos(world, this.propellerPos, getPropellerBlockOff())
                || GeneralUtils.isBlockInPos(world, this.propellerPos, getPropellerBlockOn()))
                && GeneralUtils.isBlockInPos(world, this.activatorPos, getActivatorBlock())
                && GeneralUtils.isBlockInPos(world, this.controllerPos, getControllerBlock())
                ;
    }


    @Override
    public boolean canMove(World world, EnumFacing facing, int step) {
        return  GeneralUtils.canBlockBeReplaced(world, this.propellerPos.offset(facing, step))
                && GeneralUtils.canBlockBeReplaced(world, this.activatorPos.offset(facing, step))
                && GeneralUtils.canBlockBeReplaced(world, this.controllerPos.offset(facing, step));
    }


    public void powerOn(World world) {
        if (world.getBlockState(this.propellerPos)!=null) {
            BlockFurnace.setState(true, world, this.propellerPos);
        }
    }

    public void powerOff(World world) {
        if (world.getBlockState(this.propellerPos)!=null) {
            BlockFurnace.setState(false, world, this.propellerPos);
        }
    }

    
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag("engineModule");
        this.activatorPos = GeneralUtils.readBlockPosFromNBT(tag, "activatorPos");
        this.controllerPos = GeneralUtils.readBlockPosFromNBT(tag, "controllerPos");
        this.propellerPos = GeneralUtils.readBlockPosFromNBT(tag, "propellerPos");
        this.burnTimeLeft = tag.getInteger("engineModule");
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        GeneralUtils.writeBlockPosToNBT(tagCompound, "activatorPos", activatorPos);
        GeneralUtils.writeBlockPosToNBT(tagCompound, "controllerPos", controllerPos);
        GeneralUtils.writeBlockPosToNBT(tagCompound, "propellerPos", propellerPos);
        tagCompound.setInteger("burnTimeLeft",burnTimeLeft);
        compound.setTag("engineModule", tagCompound);
    }

    public static Block getActivatorBlock() {
        return Blocks.lever;
    }

    public static Block getPropellerBlockOff() {
        return Blocks.furnace;
    }

    public static Block getPropellerBlockOn() {
        return Blocks.lit_furnace;
    }

    public static Block getControllerBlock() {
        return Blocks.noteblock;
    }



}
