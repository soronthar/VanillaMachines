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
import java.util.Collections;
import java.util.List;


class MovingMachine {
    EngineModule engine;
    RailsModule rails;
    Drill drill=new Drill() {
        @Override
        public boolean hasFinishedOperation(World world) { return true; }

        @Override
        public void powerOff(World world) {
        }

        @Override
        public void performOperation(World world, int tick) {
        }

        @Override
        public boolean isValidStructure(World world) {
            return true;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
        }

        @Override
        public void move(World world, EnumFacing facing, int step) {
        }

        @Override
        public List<BlockPos> getBlockPosList() {
            return Collections.emptyList();
        }

        @Override
        public int fuelBurn(World world) {
            return 0;
        }
    };


    List<Harvester> harvester = new ArrayList<>();
    List<Deployer> deployer = new ArrayList<>();

    public MovingMachine() {
    }

    public MovingMachine(EngineModule engine, RailsModule rails) {
        this.engine = engine;
        this.rails = rails;
    }

    public RailsModule getRails() {
        return this.rails;
    }

    private void addDrill(Drill drill) {
        this.drill = drill;
    }


    public static MovingMachine detectPoweredConstruct(World world, BlockPos activatorPos) {
        MovingMachine construct = null;
        EngineModule engine = EngineModule.detectEngineModule(world, activatorPos);
        if (engine != null) {
            BlockPos propellerPos = engine.propellerPos;
            RailsModule railsModule = RailsModule.detectRailModule(world, propellerPos);

            if (railsModule != null) {
                construct = new MovingMachine(engine, railsModule);
                SmallDrillModule smallDrillModule = SmallDrillModule.detect(world, engine.controllerPos, railsModule.facing);
                if (smallDrillModule != null) {
                    construct.addDrill(smallDrillModule);
                }
            }

        }
        return construct;
    }


    public void powerOff(World world) {
        engine.powerOff(world);
        drill.powerOff(world);
    }


    public boolean move(World world, int step) {
        EnumFacing facing = getRails().facing;
        List<BlockPos> blockPosList = this.getBlockPosList();
        if (this.isValidStructure(world) && this.canMove(world, facing, step, blockPosList) && engine.hasFuelFor(world, blockPosList.size())) {
            drill.move(world, facing, step);
            engine.move(world, facing, step);
            rails.move(world, facing, step);
            engine.burnFuel(world, blockPosList.size()); //TODO: each module burns fuel
            return true;
        } else {
            return false;
        }
    }

    public List<BlockPos> getBlockPosList() {
        List<BlockPos> constructBlocks = new ArrayList<BlockPos>();
        constructBlocks.addAll(this.engine.getBlockPosList());
        constructBlocks.addAll(this.rails.getBlockPosList());
        constructBlocks.addAll(this.drill.getBlockPosList());
        return constructBlocks;
    }

    public boolean hasFinishedOperation(World world) {
        return  drill.hasFinishedOperation(world);
    }

    public boolean canMove(World world, EnumFacing facing, int step, List<BlockPos> blockPosList) {
        boolean canMove = true;
        for (BlockPos blockPos : blockPosList) {
            BlockPos newPos = blockPos.offset(facing, step);
            canMove = canMove && (GeneralUtils.canBlockBeReplaced(world, newPos)
                    || blockPosList.contains(newPos));
        }
        return canMove && this.getRails().hasSupport(world, facing, step, blockPosList);
    }

    public boolean isValidStructure(World world) {
        return engine.isValidStructure(world)
                && getRails().isValidStructure(world);
    }

    public static BlockPos moveBlock(World world, BlockPos pos, EnumFacing facing, int step) {
        BlockPos newPos = pos.offset(facing, step);
        IBlockState state = world.getBlockState(pos);
        TileEntity tileEntity = world.getTileEntity(pos);
        ItemStack[] stackInSlot = null;
        if (tileEntity != null && tileEntity instanceof IInventory) {
            world.removeTileEntity(pos);
            IInventory inventory = (IInventory) tileEntity;
            stackInSlot = new ItemStack[inventory.getSizeInventory()];
            for (int i = 0; i < stackInSlot.length; i++) {
                stackInSlot[i] = inventory.getStackInSlot(i);
            }
        }
        doWeirdLeverFix(world, newPos, state);

        world.setBlockToAir(pos);
        world.setBlockState(newPos, state);

        if (stackInSlot != null) {
            IInventory entity = (IInventory) world.getTileEntity(newPos);
            for (int i = 0; i < stackInSlot.length; i++) {
                entity.setInventorySlotContents(i, stackInSlot[i]);
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

    public void readFromNBT(NBTTagCompound compound) {
        engine.readFromNBT(compound);
        getRails().readFromNBT(compound);
    }

    public void writeToNBT(NBTTagCompound compound) {
        engine.writeToNBT(compound);
        getRails().writeToNBT(compound);

    }


    public void performOperation(World world, int tick) {
        if (drill!= null) {
            this.drill.performOperation(world, tick);
        }
    }
}
