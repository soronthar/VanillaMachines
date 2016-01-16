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

    public RailsModule() {
    }

    private RailsModule(BlockPos[] rails, EnumFacing facing) {
        this.rails = rails;
        this.facing = facing;
    }

    public static RailsModule detectRailModule(World world, BlockPos propellerPos) {
        BlockPos[] rails = new BlockPos[4];
        RailsModule railsModule=null;
        EnumFacing railsFacing = null;

        EnumFacing[] values = {EnumFacing.NORTH, EnumFacing.WEST};
        for (int i1 = 0, valuesLength = values.length; i1 < valuesLength && railsModule == null; i1++) {
            EnumFacing facing = values[i1];
            rails[0] = propellerPos.offset(facing);
            rails[1] = propellerPos.offset(facing.getOpposite());
            if (GeneralUtils.isBlockInPos(world, rails[0], getRailsBlock()) &&
                    GeneralUtils.isBlockInPos(world, rails[1], getRailsBlock())) {
                EnumFacing[] t = {facing.rotateY(), facing.rotateYCCW()};
                for (int i = 0, tLength = t.length; i < tLength && railsModule == null; i++) {
                    EnumFacing enumFacing = t[i];
                    if (GeneralUtils.isBlockInPos(world, rails[0].offset(enumFacing), getRailsBlock()) &&
                            GeneralUtils.isBlockInPos(world, rails[1].offset(enumFacing), getRailsBlock())) {
                        rails[2] = rails[0].offset(enumFacing);
                        rails[3] = rails[1].offset(enumFacing);
                        railsFacing = enumFacing.getOpposite();
                        railsModule=new RailsModule(rails, railsFacing);
                    }
                }
            }
        }
        return railsModule;
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
