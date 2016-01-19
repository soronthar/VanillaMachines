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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


class PoweredConstruct implements Construct {
    EngineModule engine;
    List<Construct> modules =new ArrayList<Construct>();


    public PoweredConstruct() {
    }

    public PoweredConstruct(EngineModule engine) {
        this.engine = engine;
    }

    public void addRails(RailsModule rails) {
        this.modules.add(rails);
    }

    private void addModule(Construct construct) {
        this.modules.add(construct);
    }

    public RailsModule getRails() {
        return (RailsModule) modules.get(0);
    }

    public static PoweredConstruct detectPoweredConstruct(World world, BlockPos activatorPos) {
        PoweredConstruct construct = null;
        EngineModule engine = EngineModule.detectEngineModule(world, activatorPos);
        if (engine!=null) {
            construct = new PoweredConstruct(engine);
            BlockPos propellerPos = engine.propellerPos;
            RailsModule railsModule = RailsModule.detectRailModule(world, propellerPos);

            if (railsModule != null) {
                construct.addRails(railsModule);
                SmallDrillModule smallDrillModule = SmallDrillModule.detect(world, engine.controllerPos, railsModule.facing);
                if (smallDrillModule !=null) {
                    construct.addModule(smallDrillModule);
                }
            }

        }
        return construct;
    }


    public void powerOff(World world) {
        engine.powerOff(world);
        for (Construct construct : modules) {
            construct.powerOff(world);
        }
    }

    @Override
    public void move(World world, EnumFacing facing, int step) {
        for (Construct construct : modules) {
            construct.move(world, facing, step);
        }
        engine.move(world, facing, step);
    }

    public boolean move(World world, int step) {
        EnumFacing facing=getRails().facing;
        List<BlockPos> blockPosList = this.getBlockPosList();
        if (this.isValidStructure(world) && this.canMove(world, facing, step, blockPosList) && engine.hasFuelFor(world,blockPosList.size())) {
            this.move(world, facing, step);
            engine.burnFuel(world,blockPosList.size());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<BlockPos> getBlockPosList() {
        List<BlockPos> constructBlocks=new ArrayList<BlockPos>();
        constructBlocks.addAll(this.engine.getBlockPosList());
        for (Construct construct : modules) {
            constructBlocks.addAll(construct.getBlockPosList());
        }
        return constructBlocks;
    }

    @Override
    public boolean hasFinishedOperation(World world) {
        boolean result=true;
        for (Construct construct : modules) {
            result = result && construct.hasFinishedOperation(world);
        }
        return result;
    }

    @Override
    public boolean canMove(World world, EnumFacing facing, int step, List<BlockPos> blockPosList) {
        boolean canMove=true;
        for (BlockPos blockPos : blockPosList) {
            BlockPos newPos = blockPos.offset(facing, step);
            canMove = canMove && (GeneralUtils.canBlockBeReplaced(world, newPos)
                                    || blockPosList.contains(newPos));
        }
        return canMove && this.engine.canMove(world, facing, step, blockPosList) && this.getRails().canMove(world, facing, step, blockPosList); //TODO: remove the canMove method. it makes no sense
    }

    @Override
    public boolean isValidStructure(World world) {
        return engine.isValidStructure(world)
                && getRails().isValidStructure(world);
    }

    public static BlockPos moveBlock(World world, BlockPos pos, EnumFacing facing, int step) {
        BlockPos newPos = pos.offset(facing, step);
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
        world.setBlockState(newPos, state);

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
    public static void doWeirdLeverFix(World world, BlockPos pos, IBlockState state) {
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
        getRails().readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        engine.writeToNBT(compound);
        getRails().writeToNBT(compound);

    }


    public void performOperation(World world) {
        for (Construct construct : modules) {
            construct.performOperation(world);
        }
    }
}
