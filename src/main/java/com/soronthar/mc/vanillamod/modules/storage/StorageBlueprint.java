package com.soronthar.mc.vanillamod.modules.storage;

import com.soronthar.mc.vanillamod.modules.EngineModule;
import com.soronthar.mc.vanillamod.Storage;
import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class StorageBlueprint {
    public static Storage detectStorage(World world, EngineModule engine, EnumFacing facing) {
        BlockPos controllerPos = engine.controllerPos;

        BlockPos chestPos = controllerPos.offset(facing.getOpposite());

        if (GeneralUtils.isBlockInPos(world, chestPos, Blocks.chest)) {
            return new StorageModule(chestPos);
        } else {
            return new EjectStorage(controllerPos);
        }
    }
}
