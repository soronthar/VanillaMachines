package com.soronthar.mc.vanillamod.modules;

import com.soronthar.mc.vanillamod.Module;
import com.soronthar.mc.vanillamod.MovingMachine;
import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class RailsModule implements Module {
    private MovingMachine machine;
    private BlockPos[] rails;
    public EnumFacing facing;

    public static Block getRailsBlock() {
        return Blocks.planks;
    }

    public RailsModule() {
    }

    RailsModule(BlockPos[] rails, EnumFacing facing) {
        this.rails = rails;
        this.facing = facing;
    }

    @Override
    public void setMachine(MovingMachine machine) {
        this.machine=machine;
    }

    @Override
    public List<BlockPos> getBlockPosList() {
        return Arrays.asList(rails);
    }

    public boolean hasSupport(World world, EnumFacing facing, int step, List<BlockPos> blockPosList) {
        boolean canMove=true;
        for (BlockPos rail : rails) {
            BlockPos newPos = rail.offset(facing, step);
            canMove = canMove
                    && world.getBlockState(newPos.offset(EnumFacing.DOWN)).getBlock().isBlockSolid(world, newPos.offset(EnumFacing.DOWN), EnumFacing.UP);
        }
        return canMove;
    }


    @Override
    public void move(World world, EnumFacing facing, int step) {
        for (int i = 0; i < rails.length; i++) {
            rails[i] = MovingMachine.moveBlock(world, rails[i], facing, step);

        }
    }

    @Override
    public boolean isValidStructure() {
        boolean valid=true;
        for (BlockPos rail : rails) {
            valid = valid && GeneralUtils.isBlockInPos(machine.getWorld(), rail, getRailsBlock());
        }
        return valid;
    }

}
