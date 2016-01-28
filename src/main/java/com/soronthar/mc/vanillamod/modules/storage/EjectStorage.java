package com.soronthar.mc.vanillamod.modules.storage;

import com.soronthar.mc.vanillamod.MovingMachine;
import com.soronthar.mc.vanillamod.Storage;
import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.List;

public class EjectStorage implements Storage {

    private BlockPos ejectPos;
    private MovingMachine machine;

    public EjectStorage(BlockPos ejectPos) {
        this.ejectPos = ejectPos;
    }

    @Override
    public boolean addToStorage(List<ItemStack> drops) {
        GeneralUtils.eject(drops, machine.getWorld(), this.ejectPos);
        return true;
    }

    @Override
    public boolean isValidStructure() {
        return true;
    }

    @Override
    public void move(int step) {

    }

    @Override
    public List<BlockPos> getBlockPosList() {
        return Collections.emptyList();
    }

    @Override
    public void setMachine(MovingMachine machine) {
        this.machine =machine;
    }

    @Override
    public boolean addToStorage(ItemStack itemstack) {
        GeneralUtils.eject(itemstack, machine.getWorld(), this.ejectPos);
        return true;
    }
}
