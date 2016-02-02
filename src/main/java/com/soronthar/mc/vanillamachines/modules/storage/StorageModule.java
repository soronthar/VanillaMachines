package com.soronthar.mc.vanillamachines.modules.storage;

import com.soronthar.mc.vanillamachines.MovingMachine;
import com.soronthar.mc.vanillamachines.Storage;
import com.soronthar.mc.vanillamachines.util.GeneralUtils;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class StorageModule implements Storage {
    BlockPos chestPos;
    MovingMachine machine;

    public StorageModule(BlockPos chestPos) {
        this.chestPos=chestPos;
    }

    private World getWorld() {
        return machine.getWorld();
    }

    @Override
    public void setMachine(MovingMachine machine) {
        this.machine=machine;
    }

    @Override
    public boolean isValidStructure() {
        return GeneralUtils.isBlockInPos(getWorld(), this.chestPos, Blocks.chest);
    }

    @Override
    public void move(int step) {
        this.chestPos= GeneralUtils.moveBlock(getWorld(), this.chestPos, machine.getFacing(), step);
    }

    @Override
    public List<BlockPos> getBlockPosList() {
        return Collections.singletonList(chestPos);
    }

    @Override
    public boolean addToStorage(List<ItemStack> drops) {
        boolean placed=true;

        for (ItemStack itemStack : drops) {
            placed = placed && addToStorage(itemStack);
        }
        return placed;
    }


    //TODO: Partial itemstack.. if there is a slot with 16 and a stack is added, add 48 and set the rest in another slot.
    public boolean addToStorage(ItemStack itemstack) {
        boolean placed=false;
        TileEntity tileEntity = getWorld().getTileEntity(chestPos);
        if (tileEntity instanceof IInventory) {
            IInventory inventory= (IInventory) tileEntity;
            int sizeInventory = inventory.getSizeInventory();
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
                GeneralUtils.eject(itemstack, getWorld(), chestPos);
            }
        } else {
            GeneralUtils.eject(itemstack, getWorld(), chestPos);
        }
        return placed;
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
