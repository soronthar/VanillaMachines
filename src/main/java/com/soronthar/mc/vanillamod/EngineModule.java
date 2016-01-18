package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class EngineModule implements Construct {
    BlockPos activatorPos;
    BlockPos controllerPos;
    BlockPos propellerPos;

    int burnTimeLeft = 0;
    int initialFuel = 0;

    public EngineModule() {
    }

    private EngineModule(BlockPos activatorPos, BlockPos controllerPos, BlockPos propellerPos) {
        this.activatorPos = activatorPos;
        this.controllerPos = controllerPos;
        this.propellerPos = propellerPos;
    }

    public static EngineModule detectEngineModule(World world, BlockPos activatorPos) {
        EngineModule engine = null;

        EnumFacing controllerFacing = GeneralUtils.findBlockAround(world, activatorPos, getControllerBlock());
        if (controllerFacing != null) {
            BlockPos controllerPos = activatorPos.offset(controllerFacing);
            BlockPos propellerPos = controllerPos.down();
            if (isPropellerBlock(world.getBlockState(propellerPos))) {
                engine = new EngineModule(activatorPos, controllerPos, propellerPos);
                TileEntityFurnace tileEntity = (TileEntityFurnace) world.getTileEntity(propellerPos);
                engine.initialFuel = tileEntity.getStackInSlot(1).stackSize;
            }
        }
        return engine;
    }

    public void burnFuel(World world) {
        if (burnTimeLeft > 0) {
            burnTimeLeft--;
        }

        if (burnTimeLeft == 0) {
            TileEntityFurnace furnace = (TileEntityFurnace) world.getTileEntity(propellerPos);
            if (furnace != null
                    && furnace.getStackInSlot(1) != null
                    && furnace.getStackInSlot(1).stackSize > 0
                    ) {
                ItemStack itemStack = furnace.decrStackSize(1, 1);
                burnTimeLeft += TileEntityFurnace.getItemBurnTime(itemStack);
            }
        }
    }

    @Override
    public boolean isValidStructure(World world) {
        return (isPropellerBlock(world.getBlockState(this.propellerPos))
                && isActivatorBlock(world.getBlockState(this.activatorPos))
                && isControllerBlock(world.getBlockState(this.controllerPos)));
    }


    @Override
    public List<BlockPos> getBlockPosList() {
        return Arrays.asList(controllerPos, activatorPos, propellerPos);
    }

    @Override
    public boolean canMove(World world, EnumFacing facing, int step) {
        return true;
    }


    public void powerOn(World world) {
        if (!GeneralUtils.isBlockInPos(world, this.propellerPos, EngineModule.getPropellerBlockOn())) {
            BlockFurnace.setState(true, world, this.propellerPos);
        }
    }

    public void powerOff(World world) {
        if (world.getBlockState(this.propellerPos) != null) {
            BlockFurnace.setState(false, world, this.propellerPos);
        }
    }

    @Override
    public void move(World world, EnumFacing facing, int step) {
        this.activatorPos = PoweredConstruct.moveBlock(world, this.activatorPos, facing, step);
        this.controllerPos = PoweredConstruct.moveBlock(world, this.controllerPos, facing, step);
        this.propellerPos = PoweredConstruct.moveBlock(world, this.propellerPos, facing, step);
    }


    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag("engineModule");
        this.activatorPos = GeneralUtils.readBlockPosFromNBT(tag, "activatorPos");
        this.controllerPos = GeneralUtils.readBlockPosFromNBT(tag, "controllerPos");
        this.propellerPos = GeneralUtils.readBlockPosFromNBT(tag, "propellerPos");
        this.burnTimeLeft = tag.getInteger("burnTimeLeft");
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        GeneralUtils.writeBlockPosToNBT(tagCompound, "activatorPos", activatorPos);
        GeneralUtils.writeBlockPosToNBT(tagCompound, "controllerPos", controllerPos);
        GeneralUtils.writeBlockPosToNBT(tagCompound, "propellerPos", propellerPos);
        tagCompound.setInteger("burnTimeLeft", burnTimeLeft);
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


    public static boolean isActivatorBlock(IBlockState blockState) {
        return blockState != null && getActivatorBlock().equals(blockState.getBlock());
    }

    public static boolean isPropellerBlock(IBlockState blockState) {
        Block block = blockState.getBlock();
        return getPropellerBlockOff().equals(block) || getPropellerBlockOn().equals(block);
    }

    private boolean isControllerBlock(IBlockState blockState) {
        return blockState != null && getControllerBlock().equals(blockState.getBlock());
    }


    public void validateFuel(World world) {
        TileEntityFurnace tileEntity = (TileEntityFurnace) world.getTileEntity(propellerPos);
        if (tileEntity.getStackInSlot(1) == null || tileEntity.getStackInSlot(1).stackSize != initialFuel) {
            System.out.println("Fuel is wrong...");
        }
    }
}
