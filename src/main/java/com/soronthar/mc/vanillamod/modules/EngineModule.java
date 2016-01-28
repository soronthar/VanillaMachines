package com.soronthar.mc.vanillamod.modules;

import com.soronthar.mc.vanillamod.Module;
import com.soronthar.mc.vanillamod.MovingMachine;
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

    public BlockPos activatorPos;
    public BlockPos controllerPos;

    public BlockPos propellerPos;
    int burnTimeLeft = 0;

    public EngineModule() {
    }

    EngineModule(BlockPos activatorPos, BlockPos controllerPos, BlockPos propellerPos) {
        this.activatorPos = activatorPos;
        this.controllerPos = controllerPos;
        this.propellerPos = propellerPos;
    }

    public void burnFuel(int size) {
        if (burnTimeLeft <= size) {
            TileEntityFurnace furnace = (TileEntityFurnace) getWorld().getTileEntity(propellerPos);
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
    public boolean isValidStructure() {
        World world = getWorld();
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

    public void powerOn() {
        if (!GeneralUtils.isBlockInPos(getWorld(),this.propellerPos, EngineModule.getPropellerBlockOn())) {
            BlockFurnace.setState(true, getWorld(), this.propellerPos);
        }
    }

    public void powerOff() {
        if (getWorld().getBlockState(this.propellerPos) != null) {
            try {
                BlockFurnace.setState(false, getWorld(), this.propellerPos);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private World getWorld() {
        return machine.getWorld();
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

    public boolean hasFuelFor(int count) {
        TileEntityFurnace furnace = (TileEntityFurnace) getWorld().getTileEntity(propellerPos);
        ItemStack fuelStack = furnace.getStackInSlot(1);
        int additionalBurnTime = fuelStack != null ? fuelStack.stackSize * TileEntityFurnace.getItemBurnTime(fuelStack) : 0;
        return this.burnTimeLeft + additionalBurnTime >= count;
    }

    public boolean isPowered() {
        return GeneralUtils.isBlockInPos(getWorld(), propellerPos, EngineModule.getPropellerBlockOn());
    }
}
