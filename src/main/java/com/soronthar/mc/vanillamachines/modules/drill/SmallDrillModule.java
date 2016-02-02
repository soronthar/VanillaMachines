package com.soronthar.mc.vanillamachines.modules.drill;

import com.soronthar.mc.vanillamachines.Drill;
import com.soronthar.mc.vanillamachines.MovingMachine;
import com.soronthar.mc.vanillamachines.util.GeneralUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;


public class SmallDrillModule implements Drill {
    private MovingMachine machine;
    BlockPos drillHeadPos;
    BlockPos[] drillArea = new BlockPos[9];
    int currentDrillCell = 0;

    public SmallDrillModule(BlockPos drillHeadPos, EnumFacing facing) {
        this.drillHeadPos = drillHeadPos;
        calculateDrillArea(drillHeadPos, facing);
    }

    private World getWorld() {
        return machine.getWorld();
    }

    @Override
    public void setMachine(MovingMachine machine) {
        this.machine = machine;
    }

    @Override
    public List<BlockPos> getBlockPosList() {
        return Collections.singletonList(drillHeadPos);
    }


    /**
     * Looks at the 9 blocks in front of the drills, and make sure they are all "air".
     * Running water or lava will stop the drill.
     *
     * @return if the drill has finished the drilling or not.
     */
    @Override
    public boolean hasFinishedOperation() {
        boolean hasFinishedDrilling = true;
        for (BlockPos blockPos : drillArea) {
            hasFinishedDrilling = hasFinishedDrilling && getWorld().isAirBlock(blockPos);
        }

        return hasFinishedDrilling;
    }

    @Override
    public void performOperation(int tick) {
        while (this.currentDrillCell < this.drillArea.length && (getWorld().isAirBlock(getCurrentDrillBlock()) || GeneralUtils.isLiquid(getWorld(), getCurrentDrillBlock()))) {
            this.currentDrillCell++;
        }

        if (this.currentDrillCell < this.drillArea.length) {
            boolean added = drill();
            if (!added) {
                machine.powerOff();
            }
        } else {
            this.currentDrillCell = 0;
        }
    }

    private boolean drill() {
        IBlockState blockState = getWorld().getBlockState(getCurrentDrillBlock());
        boolean added = this.machine.storage.addToStorage(blockState.getBlock().getDrops(getWorld(), getCurrentDrillBlock(), blockState, 0));
        TileEntity entity = getWorld().getTileEntity(getCurrentDrillBlock());
        if (entity != null && entity instanceof IInventory) {
            IInventory inventory = (IInventory) entity;
            int sizeInventory = inventory.getSizeInventory();
            for (int i = 0; i < sizeInventory; i++) {
                ItemStack stackInSlot = inventory.removeStackFromSlot(i);
                if (stackInSlot != null) {
                    added = added && this.machine.storage.addToStorage(stackInSlot);
                }
            }

        }
        getWorld().destroyBlock(getCurrentDrillBlock(), false);
        return added;
    }

    private BlockPos getCurrentDrillBlock() {
        return this.drillArea[this.currentDrillCell];
    }


    @Override
    public boolean isValidStructure() {
        return GeneralUtils.isBlockInPos(getWorld(), this.drillHeadPos, DrillBlueprint.getDrillHeadBlock());
    }

    @Override
    public void powerOff() {
        currentDrillCell = 0;
    }


    @Override
    public int fuelBurn() {
        return 1;
    }

    @Override
    public void move(int step) {
        this.drillHeadPos = GeneralUtils.moveBlock(getWorld(), this.drillHeadPos, machine.getFacing(), step);
        calculateDrillArea(drillHeadPos, machine.getFacing());
        currentDrillCell = 0;
    }

    private void calculateDrillArea(BlockPos drillHeadPos, EnumFacing facing) {
        BlockPos drillAnchor = drillHeadPos.offset(facing);
        BlockPos anchorLeft = drillAnchor.offset(facing.rotateYCCW());
        BlockPos anchorRight = drillAnchor.offset(facing.rotateY());
        this.drillArea[0] = anchorLeft.down();
        this.drillArea[1] = drillAnchor.down();
        this.drillArea[2] = anchorRight.down();
        this.drillArea[3] = anchorLeft;
        this.drillArea[4] = drillAnchor;
        this.drillArea[5] = anchorRight;
        this.drillArea[6] = anchorLeft.up();
        this.drillArea[7] = drillAnchor.up();
        this.drillArea[8] = anchorRight.up();
    }

}
