package com.soronthar.mc.vanillamachines.modules.drill;

import com.soronthar.mc.vanillamachines.Drill;
import com.soronthar.mc.vanillamachines.MovingMachine;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.*;

public class BigDrillModule implements Drill {
    private Map<BlockPos, Block> bluePrint;
    private MovingMachine machine;
    private BlockPos drillHeadPos;


    public BigDrillModule(BlockPos drillHeadPos, EnumFacing facing) {
        this.drillHeadPos = drillHeadPos;
        this.bluePrint = DrillBlueprint.calculateBlockPosForDrill(drillHeadPos, facing);
    }

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
        EnumFacing facing = machine.getFacing();
        Map<BlockPos, Block> newPrint=new HashMap<BlockPos, Block>();

        Set<BlockPos> blockPoses = this.bluePrint.keySet();


        for (Map.Entry<BlockPos, Block> entry : this.bluePrint.entrySet()) {
            BlockPos blockPos = entry.getKey();
            BlockPos newPos = blockPos.add(facing.getDirectionVec());
            Block block = entry.getValue();
            this.machine.getWorld().setBlockToAir(blockPos);

            newPrint.put(newPos, block);
        }

        this.bluePrint=newPrint;

        for (Map.Entry<BlockPos, Block> entry : this.bluePrint.entrySet()) {
            machine.getWorld().setBlockState(entry.getKey(), entry.getValue().getDefaultState());
        }
    }

    @Override
    public List<BlockPos> getBlockPosList() {
        return new ArrayList<BlockPos>(this.bluePrint.keySet());
    }

    @Override
    public void setMachine(MovingMachine machine) {
        this.machine=machine;
    }

    @Override
    public void powerOff() {

    }

    @Override
    public int fuelBurn() {
        return 5;
    }
}
