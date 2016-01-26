package com.soronthar.mc.vanillamod.modules.storage;

import com.soronthar.mc.vanillamod.MovingMachine;
import com.soronthar.mc.vanillamod.Storage;
import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class EjectStorage implements Storage {

    private BlockPos ejectPos;

    public EjectStorage(BlockPos ejectPos) {
        this.ejectPos = ejectPos;
    }

    @Override
    public void addToStorage(World world, List<ItemStack> drops) {
        GeneralUtils.eject(drops, world, this.ejectPos);
    }

    @Override
    public boolean isValidStructure(World world) {
        return true;
    }

    @Override
    public void move(World world, EnumFacing facing, int step) {

    }

    @Override
    public List<BlockPos> getBlockPosList() {
        return Collections.emptyList();
    }

    @Override
    public void setMachine(MovingMachine machine) {
    }
}
