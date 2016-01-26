package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class EngineModule implements Module {
    private MovingMachine machine;

    BlockPos activatorPos;
    public BlockPos controllerPos;

    BlockPos propellerPos;
    int burnTimeLeft = 0;

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
            }
        }
        return engine;
    }

    public void burnFuel(World world, int size) {


        if (burnTimeLeft <= size) {
            TileEntityFurnace furnace = (TileEntityFurnace) world.getTileEntity(propellerPos);
            if (furnace != null
                    && furnace.getStackInSlot(1) != null
                    && furnace.getStackInSlot(1).stackSize > 0
                    ) {
                ItemStack itemStack = furnace.decrStackSize(1, 1);
                burnTimeLeft += TileEntityFurnace.getItemBurnTime(itemStack);
            }
        }

        burnTimeLeft -= size;
        System.out.println("burnTimeLeft = " + burnTimeLeft);
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
    public void setMachine(MovingMachine machine) {
        this.machine=machine;
    }

    public void powerOn(World world) {
        if (!GeneralUtils.isBlockInPos(world, this.propellerPos, EngineModule.getPropellerBlockOn())) {
            BlockFurnace.setState(true, world, this.propellerPos);
        }
    }

    public void powerOff(World world) {
        if (world.getBlockState(this.propellerPos) != null) {
            try {
                BlockFurnace.setState(false, world, this.propellerPos);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void move(World world, EnumFacing facing, int step) {
        this.activatorPos = MovingMachine.moveBlock(world, this.activatorPos, facing, step);
        this.controllerPos = MovingMachine.moveBlock(world, this.controllerPos, facing, step);
        this.propellerPos = MovingMachine.moveBlock(world, this.propellerPos, facing, step);
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

    public boolean hasFuelFor(World world, int count) {
        TileEntityFurnace furnace = (TileEntityFurnace) world.getTileEntity(propellerPos);
        ItemStack fuelStack = furnace.getStackInSlot(1);
        int additionalBurnTime = fuelStack != null ? fuelStack.stackSize * TileEntityFurnace.getItemBurnTime(fuelStack) : 0;
        return this.burnTimeLeft + additionalBurnTime >= count;
    }

    public boolean isPowered(World world) {
        return GeneralUtils.isBlockInPos(world,propellerPos, EngineModule.getPropellerBlockOn());
    }
}
