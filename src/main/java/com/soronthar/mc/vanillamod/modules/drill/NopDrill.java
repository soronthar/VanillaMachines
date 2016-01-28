package com.soronthar.mc.vanillamod.modules.drill;

import com.soronthar.mc.vanillamod.Drill;
import com.soronthar.mc.vanillamod.MovingMachine;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.List;

public class NopDrill implements Drill {

    public static Drill instance=new NopDrill();

    @Override
    public boolean hasFinishedOperation() {
        return true;
    }

    @Override
    public void performOperation(int tick) {
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
    }

    @Override
    public void powerOff() {
    }

    @Override
    public int fuelBurn() {
        return 0;
    }
}
