package com.soronthar.mc.vanillamod.modules.drill;

import com.soronthar.mc.vanillamod.Drill;
import com.soronthar.mc.vanillamod.MovingMachine;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

/**
 * Created by pc on 26/01/2016.
 */
public class NopDrill implements Drill {

    public static Drill instance=new NopDrill();

    @Override
    public boolean hasFinishedOperation(World world) {
        return true;
    }

    @Override
    public void performOperation(World world, int tick) {
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

    @Override
    public void powerOff(World world) {
    }

    @Override
    public int fuelBurn(World world) {
        return 0;
    }
}
