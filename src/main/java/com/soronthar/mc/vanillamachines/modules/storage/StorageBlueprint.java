package com.soronthar.mc.vanillamachines.modules.storage;

import com.soronthar.mc.vanillamachines.modules.EngineModule;
import com.soronthar.mc.vanillamachines.Storage;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class StorageBlueprint {
    public static Storage detectStorage(World world, EngineModule engine, EnumFacing facing) {
        BlockPos controllerPos = engine.controllerPos;
        BlockPos storagePos = controllerPos.offset(facing.getOpposite());
        TileEntity entity = world.getTileEntity(storagePos);

        if (entity!=null && entity instanceof IInventory) {
            return new StorageModule(storagePos);
        } else {
            return new EjectStorage(controllerPos);
        }
    }
}
