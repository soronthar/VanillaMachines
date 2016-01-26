package com.soronthar.mc.vanillamod.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockMushroom;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

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

    public static void eject(ItemStack itemstack, World world, BlockPos pos) {
        if (itemstack != null) {
            Random random = new Random();

            float xCoord=pos.getX();
            float yCoord=pos.getY();
            float zCoord=pos.getZ();

            float f = random.nextFloat() * 0.8F + 0.1F;
            float f1 = random.nextFloat() * 0.8F + 0.1F;
            float f2 = random.nextFloat() * 0.8F + 0.1F;
            float f3 = 0.05F;
            int i1 = itemstack.stackSize;
            EntityItem entityitem = new EntityItem(world, xCoord + f, yCoord + 1 + f1, zCoord + f2,
                    new ItemStack(itemstack.getItem(), i1, itemstack.getItemDamage()));
            entityitem.motionX = (float) random.nextGaussian() * f3;
            entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
            entityitem.motionZ = (float) random.nextGaussian() * f3;
            if (itemstack.hasTagCompound()) {
                entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
            }
            world.spawnEntityInWorld(entityitem);
        }
    }

    public static void eject(List<ItemStack> drops, World world, BlockPos pos) {
        for (ItemStack itemStack : drops) {
            GeneralUtils.eject(itemStack, world, pos);
        }

    }
}
