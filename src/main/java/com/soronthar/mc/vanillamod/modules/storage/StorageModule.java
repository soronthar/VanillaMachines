package com.soronthar.mc.vanillamod.modules.storage;

import com.soronthar.mc.vanillamod.MovingMachine;
import com.soronthar.mc.vanillamod.Storage;
import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class StorageModule implements Storage {
    BlockPos chestPos;
    MovingMachine machine;

    public StorageModule(BlockPos chestPos) {
        this.chestPos=chestPos;
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

    @Override
    public boolean addToStorage(World world, List<ItemStack> drops) {
        boolean placed=true;

        for (ItemStack itemStack : drops) {
            placed = placed && addToStorage(world, itemStack);
        }
        return placed;
    }


    //TODO: Partial itemstack.. if there is a slot with 16 and a stack is added, add 48 and set the rest in another slot.
    public boolean addToStorage(World world, ItemStack itemstack) {
        boolean placed=false;
        TileEntity tileEntity = world.getTileEntity(chestPos);
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
                GeneralUtils.eject(itemstack, world, chestPos);
            }
        } else {
            GeneralUtils.eject(itemstack, world, chestPos);
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
