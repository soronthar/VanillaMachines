package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class SmallDrillModule implements Drill {
    BlockPos drillHeadPos;
    BlockPos[] drillArea=new BlockPos[9];
    int currentDrillCell=0;

    public SmallDrillModule(BlockPos drillHeadPos, EnumFacing facing) {
        this.drillHeadPos = drillHeadPos;
        calculateDrillArea(drillHeadPos, facing);
    }


    public static SmallDrillModule detect(World world, BlockPos controllerPos, EnumFacing facing) {
        BlockPos drillHeadPos = controllerPos.offset(facing);
        if (GeneralUtils.isBlockInPos(world, drillHeadPos, Blocks.iron_block)) {
            return new SmallDrillModule(drillHeadPos, facing);
        } else {
            return null;
        }
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
        while(this.currentDrillCell<this.drillArea.length && (world.isAirBlock(getCurrentDrillBlock()) || GeneralUtils.isLiquid(world,getCurrentDrillBlock()))) {
            this.currentDrillCell++;
        }

        if (this.currentDrillCell<this.drillArea.length) {
            IBlockState blockState = world.getBlockState(getCurrentDrillBlock());
            blockState.getBlock().dropBlockAsItem(world, getCurrentDrillBlock(), blockState, 0);
            world.destroyBlock(getCurrentDrillBlock(),false);
        } else {
            this.currentDrillCell=0;
        }
    }

    private BlockPos getCurrentDrillBlock() {
        return this.drillArea[this.currentDrillCell];
    }


    @Override
    public boolean isValidStructure(World world) {
        return GeneralUtils.isBlockInPos(world, this.drillHeadPos, Blocks.iron_block); //TODO: encapsulate the iron_block thingie
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag("smallDrillModule");
        this.drillHeadPos = GeneralUtils.readBlockPosFromNBT(tag, "drillHeadPos");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        GeneralUtils.writeBlockPosToNBT(tag, "drillHeadPos", this.drillHeadPos);
        compound.setTag("smallDrillModule", tag);
    }

    @Override
    public void powerOff(World world) {
        currentDrillCell=0;
    }


    @Override
    public int fuelBurn(World world) {
        return 1;
    }

    @Override
    public void move(World world, EnumFacing facing, int step) {
        this.drillHeadPos = MovingMachine.moveBlock(world, this.drillHeadPos, facing, step);
        calculateDrillArea(drillHeadPos, facing);
    }

    private void calculateDrillArea(BlockPos drillHeadPos, EnumFacing facing) {
        BlockPos drillAnchor = drillHeadPos.offset(facing);
        BlockPos anchorLeft=drillAnchor.offset(facing.rotateYCCW());
        BlockPos anchorRight=drillAnchor.offset(facing.rotateY());
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
