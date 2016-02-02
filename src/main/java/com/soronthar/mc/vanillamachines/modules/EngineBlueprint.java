package com.soronthar.mc.vanillamachines.modules;

import com.soronthar.mc.vanillamachines.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EngineBlueprint {
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
        Block block = blockState != null ? blockState.getBlock():null;
        return getPropellerBlockOff().equals(block) || getPropellerBlockOn().equals(block);
    }

    public static boolean isControllerBlock(IBlockState blockState) {
        return blockState != null && getControllerBlock().equals(blockState.getBlock());
    }
}
