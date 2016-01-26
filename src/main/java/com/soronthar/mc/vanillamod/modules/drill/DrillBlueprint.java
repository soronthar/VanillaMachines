package com.soronthar.mc.vanillamod.modules.drill;

import com.soronthar.mc.vanillamod.Drill;
import com.soronthar.mc.vanillamod.modules.drill.SmallDrillModule;
import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DrillBlueprint {
    public static Drill detect(World world, BlockPos controllerPos, EnumFacing facing) {
        BlockPos drillHeadPos = controllerPos.offset(facing);
        if (GeneralUtils.isBlockInPos(world, drillHeadPos, getDrillHeadBlock())) {
            return new SmallDrillModule(drillHeadPos, facing);
        } else {
            return NopDrill.instance;
        }
    }

    public static Block getDrillHeadBlock() {
        return Blocks.iron_block;
    }
}
