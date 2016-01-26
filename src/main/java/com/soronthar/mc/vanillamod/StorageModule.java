package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import com.sun.deploy.util.GeneralUtil;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class StorageModule implements Module {
    BlockPos chestPos;
    MovingMachine machine;

    public StorageModule(BlockPos chestPos) {
        this.chestPos=chestPos;
    }

    public static StorageModule detectStorage(World world, EngineModule engine, EnumFacing facing) {
        BlockPos controllerPos = engine.controllerPos;

        BlockPos chestPos = controllerPos.offset(facing.getOpposite());

        if (GeneralUtils.isBlockInPos(world, chestPos, Blocks.chest)) {
            return new StorageModule(chestPos);
        } else {
            return null;
        }
    }

    @Override
    public void setMachine(MovingMachine machine) {
        this.machine=machine;
    }

    @Override
    public boolean isValidStructure(World world) {
        return GeneralUtils.isBlockInPos(world, this.chestPos, Blocks.chest);
    }

    @Override
    public void move(World world, EnumFacing facing, int step) {
        this.chestPos=MovingMachine.moveBlock(world, this.chestPos, facing, step);
    }

    @Override
    public List<BlockPos> getBlockPosList() {
        return Collections.singletonList(chestPos);
    }

    public void addToStorage(World world, List<ItemStack> drops) {
        for (ItemStack itemStack : drops) {
            addToStorage(world, itemStack);
        }
    }

    private void addToStorage(World world, ItemStack itemstack) {
        TileEntity tileEntity = world.getTileEntity(chestPos);
        if (tileEntity instanceof IInventory) {
            IInventory inventory= (IInventory) tileEntity;
            int sizeInventory = inventory.getSizeInventory();
            boolean placed=false;
            for (int i = 0; i < sizeInventory && !placed; i++) {
                ItemStack stackInSlot = inventory.getStackInSlot(i);
                if (stackInSlot!=null
                        && sameItem(itemstack, stackInSlot)
                        && canStack(inventory, itemstack, stackInSlot)) {
                    stackInSlot.stackSize+=itemstack.stackSize;
                    inventory.setInventorySlotContents(i, stackInSlot);
                    placed=true;
                } else if (stackInSlot==null) {
                    inventory.setInventorySlotContents(i, itemstack);
                    placed=true;
                }
            }

            if (placed) {
                inventory.markDirty();
            } else {
                GeneralUtils.eject(itemstack, world, chestPos);
            }
        } else {
            GeneralUtils.eject(itemstack, world, chestPos);
        }
    }

    private boolean canStack(IInventory inventory, ItemStack itemstack, ItemStack stackInSlot) {
        return stackInSlot.isStackable() && itemstack.isStackable()
                && stackInSlot.stackSize + itemstack.stackSize <= inventory.getInventoryStackLimit();
    }

    private boolean sameItem(ItemStack itemstack, ItemStack stackInSlot) {
        return stackInSlot.getItem().equals(itemstack.getItem())
                && stackInSlot.getMetadata()==itemstack.getMetadata();
    }

}
