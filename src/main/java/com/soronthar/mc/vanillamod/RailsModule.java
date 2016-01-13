package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

class RailsModule implements Construct{
    BlockPos[] rails;
    EnumFacing facing;

    public static Block getRailsBlock() {
        return Blocks.planks;
    }

    public RailsModule(BlockPos[] rails, EnumFacing facing) {
        this.rails = rails;
        this.facing = facing;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag("railsModule");
        this.facing=EnumFacing.getFront(tag.getInteger("railsFacing"));
        for (int i = 0; i < rails.length; i++) {
            rails[i]=GeneralUtils.readBlockPosFromNBT(tag,"rails."+i);
        }
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag =new NBTTagCompound();
        tag.setInteger("railsFacing", facing.getIndex());
        for (int i = 0; i < rails.length; i++) {
            GeneralUtils.writeBlockPosToNBT(tag, "rails." + i, rails[i]);
        }
        compound.setTag("railsModule",tag);
    }

    @Override
    public boolean canMove(World world, EnumFacing facing, int step) {
        boolean canMove=true;
        for (BlockPos rail : rails) {
            BlockPos newPos = rail.offset(facing, step);
            canMove = canMove
                    && (GeneralUtils.canBlockBeReplaced(world, newPos)
                        || GeneralUtils.isBlockInPos(world, newPos, getRailsBlock()))
                    && world.getBlockState(newPos.offset(EnumFacing.DOWN)).getBlock().isBlockSolid(world, newPos.offset(EnumFacing.DOWN), EnumFacing.UP);
        }
        return canMove;
    }


    @Override
    public boolean isValidStructure(World world) {
        boolean valid=true;
        for (BlockPos rail : rails) {
            valid = valid && GeneralUtils.isBlockInPos(world, rail, getRailsBlock());
        }
        return valid;
    }

}
