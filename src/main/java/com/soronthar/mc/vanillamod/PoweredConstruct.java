package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;


class PoweredConstruct implements Construct {
    EngineModule engine;
    RailsModule rails;

    public PoweredConstruct() {
    }

    public PoweredConstruct(EngineModule engine, RailsModule rails) {
        this.engine = engine;
        this.rails = rails;
    }

    public static PoweredConstruct detectPoweredConstruct(World world, BlockPos activatorPos) {
        PoweredConstruct construct = null;
        EngineModule engine = EngineModule.detectEngineModule(world, activatorPos);
        if (engine!=null) {
            BlockPos propellerPos = engine.propellerPos;
            RailsModule railsModule = RailsModule.detectRailModule(world, propellerPos);

            if (railsModule != null) {
                construct = new PoweredConstruct(engine,railsModule);
            }

        }
        return construct;
    }


    public boolean move(World world, int step) {
        if (this.isValidStructure(world) && this.canMove(world, rails.facing, step)) {
            engine.activatorPos = moveBlock(world, engine.activatorPos, step);
            engine.controllerPos = moveBlock(world, engine.controllerPos, step);
            engine.propellerPos = moveBlock(world, engine.propellerPos, step);
            rails.rails[0] = moveBlock(world, rails.rails[0], step);
            rails.rails[1] = moveBlock(world, rails.rails[1], step);
            rails.rails[2] = moveBlock(world, rails.rails[2], step);
            rails.rails[3] = moveBlock(world, rails.rails[3], step);
            engine.burnFuel(world);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canMove(World world, EnumFacing facing, int step) {
        return this.engine.canMove(world, rails.facing, step) && this.rails.canMove(world, rails.facing, step);
    }

    @Override
    public boolean isValidStructure(World world) {
        return engine.isValidStructure(world)
                && rails.isValidStructure(world);
    }

    private BlockPos moveBlock(World world, BlockPos pos, int step) {
        BlockPos newPos = pos.offset(rails.facing, step);
        IBlockState state = world.getBlockState(pos);
        TileEntity tileEntity = world.getTileEntity(pos);
        ItemStack[] stackInSlot=null;
        if (tileEntity!=null && tileEntity instanceof IInventory) {
            world.removeTileEntity(pos);
            IInventory inventory = (IInventory) tileEntity;
            stackInSlot=new ItemStack[inventory.getSizeInventory()];
            for (int i = 0; i < stackInSlot.length; i++) {
                stackInSlot[i]=inventory.getStackInSlot(i);
            }
        }
        doWeirdLeverFix(world, newPos, state);

        world.setBlockToAir(pos);
        world.markBlockForUpdate(pos);

        world.setBlockState(newPos, state);
        world.markBlockForUpdate(newPos);

        if (stackInSlot!=null) {
            IInventory entity = (IInventory)world.getTileEntity(newPos);
            for (int i = 0; i < stackInSlot.length; i++) {
                entity.setInventorySlotContents(i,stackInSlot[i]);
            }
        }

        return newPos;
    }

    /**
     * If the lever is placed above grass, it will be destroyed as soon as it is placed.
     * Prevent this by setting the space to air if possible.
     */
    private void doWeirdLeverFix(World world, BlockPos pos, IBlockState state) {
        if (state.getBlock().equals(Blocks.lever)
                && !GeneralUtils.isBlockInPos(world, pos.offset(EnumFacing.DOWN), Blocks.air)
                && GeneralUtils.canBlockBeReplaced(world, pos.offset(EnumFacing.DOWN))) {
            world.setBlockToAir(pos.offset(EnumFacing.DOWN));
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        engine.readFromNBT(compound);
        rails.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        engine.writeToNBT(compound);
        rails.writeToNBT(compound);

    }
}
