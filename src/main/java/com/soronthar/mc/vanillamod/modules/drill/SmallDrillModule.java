package com.soronthar.mc.vanillamod.modules.drill;

import com.soronthar.mc.vanillamod.Drill;
import com.soronthar.mc.vanillamod.MovingMachine;
import com.soronthar.mc.vanillamod.util.GeneralUtils;
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
     * @param world
     * @return if the drill has finished the drilling or not.
     */
    @Override
    public boolean hasFinishedOperation(World world) {
        boolean hasFinishedDrilling = true;
        for (BlockPos blockPos : drillArea) {
            hasFinishedDrilling = hasFinishedDrilling && world.isAirBlock(blockPos);
        }

        return hasFinishedDrilling;
    }

    @Override
    public void performOperation(World world, int tick) {
        while (this.currentDrillCell < this.drillArea.length && (world.isAirBlock(getCurrentDrillBlock()) || GeneralUtils.isLiquid(world, getCurrentDrillBlock()))) {
            this.currentDrillCell++;
        }

        if (this.currentDrillCell < this.drillArea.length) {
            boolean added = drill(world);
            if (!added) {
                machine.powerOff(world);
            }
        } else {
            this.currentDrillCell = 0;
        }
    }

    private boolean drill(World world) {
        IBlockState blockState = world.getBlockState(getCurrentDrillBlock());
        boolean added = this.machine.storage.addToStorage(world, blockState.getBlock().getDrops(world, getCurrentDrillBlock(), blockState, 0));
        TileEntity entity = world.getTileEntity(getCurrentDrillBlock());
        if (entity!=null && entity instanceof IInventory) {
            IInventory inventory= (IInventory) entity;
            int sizeInventory = inventory.getSizeInventory();
            for (int i = 0; i < sizeInventory; i++) {
                ItemStack stackInSlot = inventory.removeStackFromSlot(i);
                if (stackInSlot!=null) {
                    added = added &&  this.machine.storage.addToStorage(world,stackInSlot);
                }
            }

        }
        world.destroyBlock(getCurrentDrillBlock(), false);
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
    public void powerOff(World world) {
        currentDrillCell = 0;
    }


    @Override
    public int fuelBurn(World world) {
        return 1;
    }

    @Override
    public void move(EnumFacing facing, int step) {
        this.drillHeadPos = MovingMachine.moveBlock(getWorld(), this.drillHeadPos, facing, step);
        calculateDrillArea(drillHeadPos, facing);
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
