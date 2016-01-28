package com.soronthar.mc.vanillamod.modules;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
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
            if (GeneralUtils.isBlockInPos(world, rails[0], RailsModule.getRailsBlock()) &&
                    GeneralUtils.isBlockInPos(world, rails[1], RailsModule.getRailsBlock())) {
                EnumFacing[] t = {facing.rotateY(), facing.rotateYCCW()};
                for (int i = 0, tLength = t.length; i < tLength && railsModule == null; i++) {
                    EnumFacing enumFacing = t[i];
                    if (GeneralUtils.isBlockInPos(world, rails[0].offset(enumFacing), RailsModule.getRailsBlock()) &&
                            GeneralUtils.isBlockInPos(world, rails[1].offset(enumFacing), RailsModule.getRailsBlock())) {
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
}
