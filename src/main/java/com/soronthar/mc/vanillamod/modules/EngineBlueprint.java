package com.soronthar.mc.vanillamod.modules;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EngineBlueprint {
    public static EngineModule detectEngineModule(World world, BlockPos activatorPos) {
        EngineModule engine = null;

        EnumFacing controllerFacing = GeneralUtils.findBlockAround(world, activatorPos, EngineModule.getControllerBlock());
        if (controllerFacing != null) {
            BlockPos controllerPos = activatorPos.offset(controllerFacing);
            BlockPos propellerPos = controllerPos.down();
            if (EngineModule.isPropellerBlock(world.getBlockState(propellerPos))) {
                engine = new EngineModule(activatorPos, controllerPos, propellerPos);
                TileEntityFurnace tileEntity = (TileEntityFurnace) world.getTileEntity(propellerPos);
            }
        }
        return engine;
    }
}
