package com.soronthar.mc.vanillamod.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockMushroom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class GeneralUtils {
    public static EnumFacing findBlockAround(World world, BlockPos pos, Block block) {
        EnumFacing[] values = EnumFacing.values();
        for (EnumFacing facing : values) {
            if (world.getBlockState(pos.offset(facing)).getBlock().equals(block)) {
                return facing;
            }
        }
        return null;
    }

    public static void sendChatMessage(EntityPlayer entityPlayer, String s) {
        entityPlayer.addChatComponentMessage(new ChatComponentText(s));
    }

    public static boolean isBlockInPos(World world, BlockPos pos, Block block) {
        return block.equals(world.getBlockState(pos).getBlock());
    }

    public static void writeBlockPosToNBT(NBTTagCompound compound, String key, BlockPos pos) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setInteger("x", pos.getX());
        tagCompound.setInteger("y", pos.getY());
        tagCompound.setInteger("z", pos.getZ());
        compound.setTag(key, compound);
    }

    public static BlockPos readBlockPosFromNBT(NBTTagCompound compound, String key) {
        NBTTagCompound tag = compound.getCompoundTag(key);
        return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
    }

    public static boolean canBlockBeReplaced(World world, BlockPos pos) {
        if (!world.isAreaLoaded(pos,1)) return false;
        Block block = world.getBlockState(pos).getBlock();
        return block.isReplaceable(world, pos)
                || block.equals(Blocks.brown_mushroom)
                || block.equals(Blocks.brown_mushroom)
                || block.equals(Blocks.deadbush)
                || block instanceof BlockDoublePlant
                || block instanceof BlockFlower;
    }

    public static boolean isLiquid(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().getMaterial().isLiquid();
    }
}
