package com.soronthar.mc.vanillamachines.modules;

import com.soronthar.mc.vanillamachines.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class RailsBlueprint {
    public static RailsModule detectRailModule(World world, BlockPos propellerPos) {
        BlockPos[] rails = new BlockPos[4];
        RailsModule railsModule =null;
        EnumFacing railsFacing = null;

        EnumFacing[] values = {EnumFacing.NORTH, EnumFacing.WEST};
        for (int i1 = 0, valuesLength = values.length; i1 < valuesLength && railsModule == null; i1++) {
            EnumFacing facing = values[i1];
            rails[0] = propellerPos.offset(facing);
            rails[1] = propellerPos.offset(facing.getOpposite());
            if (GeneralUtils.isBlockInPos(world, rails[0], getRailsBlock()) &&
                    GeneralUtils.isBlockInPos(world, rails[1], getRailsBlock())) {
                EnumFacing[] t = {facing.rotateY(), facing.rotateYCCW()};
                for (int i = 0, tLength = t.length; i < tLength && railsModule == null; i++) {
                    EnumFacing enumFacing = t[i];
                    if (GeneralUtils.isBlockInPos(world, rails[0].offset(enumFacing), getRailsBlock()) &&
                            GeneralUtils.isBlockInPos(world, rails[1].offset(enumFacing), getRailsBlock())) {
                        rails[2] = rails[0].offset(enumFacing);
                        rails[3] = rails[1].offset(enumFacing);
                        railsFacing = enumFacing.getOpposite();
                        railsModule =new RailsModule(rails, railsFacing);
                    }
                }
            }
        }
        return railsModule;
    }

    public static Block getRailsBlock() {
        return Blocks.planks;
    }
}
