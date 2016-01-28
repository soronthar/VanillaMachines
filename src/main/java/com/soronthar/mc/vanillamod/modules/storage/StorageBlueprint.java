package com.soronthar.mc.vanillamod.modules.storage;

import com.soronthar.mc.vanillamod.modules.EngineBlueprint;
import com.soronthar.mc.vanillamod.Storage;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class StorageBlueprint {
    public static Storage detectStorage(World world, EngineBlueprint engine, EnumFacing facing) {
        BlockPos controllerPos = engine.controllerPos;

        BlockPos storagePos = controllerPos.offset(facing.getOpposite());
        TileEntity entity = world.getTileEntity(storagePos);

        if (entity!=null && entity instanceof IInventory) {
//        if (GeneralUtils.isBlockInPos(world, storagePos, Blocks.chest)) {
            return new StorageModule(storagePos);
        } else {
            return new EjectStorage(controllerPos);
        }
    }
}
