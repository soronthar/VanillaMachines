package com.soronthar.mc.vanillamachines.modules;

import com.soronthar.mc.vanillamachines.Module;
import com.soronthar.mc.vanillamachines.MovingMachine;
import com.soronthar.mc.vanillamachines.util.GeneralUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class RailsModule implements Module {
    private MovingMachine machine;
    private BlockPos[] rails;
    public EnumFacing facing;

    RailsModule(BlockPos[] rails, EnumFacing facing) {
        this.rails = rails;
        this.facing = facing;
    }

    private World getWorld() {
        return machine.getWorld();
    }

    @Override
    public void setMachine(MovingMachine machine) {
        this.machine=machine;
    }

    @Override
    public List<BlockPos> getBlockPosList() {
        return Arrays.asList(rails);
    }

    public boolean hasSupport(int step, List<BlockPos> blockPosList) {
        boolean canMove=true;
        for (BlockPos rail : rails) {
            BlockPos newPos = rail.offset(this.facing, step);
            canMove = canMove
                    && getWorld().getBlockState(newPos.offset(EnumFacing.DOWN)).getBlock().isBlockSolid(getWorld(), newPos.offset(EnumFacing.DOWN), EnumFacing.UP);
        }
        return canMove;
    }

    @Override
    public void move(int step) {
        for (int i = 0; i < rails.length; i++) {
            rails[i] = GeneralUtils.moveBlock(getWorld(), rails[i], facing, step);

        }
    }

    @Override
    public boolean isValidStructure() {
        boolean valid=true;
        for (BlockPos rail : rails) {
            valid = valid && GeneralUtils.isBlockInPos(getWorld(), rail, RailsBlueprint.getRailsBlock());
        }
        return valid;
    }

}
