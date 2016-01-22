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
    //TODO remove these hacks that prevents NPE
    Drill drill = new Drill() {
        @Override
        public boolean hasFinishedOperation(World world) {
            return true;
        }

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
    StorageModule storage = new StorageModule(null) {
        @Override
        public boolean isValidStructure(World world) {
            return true;
        }

        @Override
        public void move(World world, EnumFacing facing, int step) {
        }

        @Override
        public List<BlockPos> getBlockPosList() {
            return Collections.emptyList();
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

    private void addDrill(Drill drill) {
        this.drill = drill;
    }

    private void addStorage(StorageModule storage) {
        this.storage = storage;
    }


    public static MovingMachine detectMovingMachine(World world, BlockPos activatorPos) {
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

                StorageModule storage = StorageModule.detectStorage(world, engine, railsModule.facing);
                if (storage != null) {
                    construct.addStorage(storage);
                }
            }

        }
        return construct;
    }


    public boolean isValidStructure(World world) {
        return engine.isValidStructure(world)
                && rails.isValidStructure(world);
    }

    public void powerOff(World world) {
        engine.powerOff(world);
        drill.powerOff(world);
    }

    public boolean canMove(World world, EnumFacing facing, int step, List<BlockPos> blockPosList) {
        boolean canMove = true;
        for (BlockPos blockPos : blockPosList) {
            BlockPos newPos = blockPos.offset(facing, step);
            canMove = canMove && (GeneralUtils.canBlockBeReplaced(world, newPos)
                    || blockPosList.contains(newPos));
        }
        return canMove && this.rails.hasSupport(world, facing, step, blockPosList);
    }

    public List<BlockPos> getBlockPosList() {
        List<BlockPos> constructBlocks = new ArrayList<BlockPos>();
        constructBlocks.addAll(this.engine.getBlockPosList());
        constructBlocks.addAll(this.rails.getBlockPosList());

        if (this.drill!=null) {
            constructBlocks.addAll(this.drill.getBlockPosList());
        }
        if (this.storage!=null) {
            constructBlocks.addAll(this.storage.getBlockPosList());
        }
        return constructBlocks;
    }

    public boolean move(World world, int step) {
        EnumFacing facing = rails.facing;
        List<BlockPos> blockPosList = this.getBlockPosList();
        if (this.isValidStructure(world) && this.canMove(world, facing, step, blockPosList) && engine.hasFuelFor(world, blockPosList.size())) {
            drill.move(world, facing, step);
            engine.move(world, facing, step);
            rails.move(world, facing, step);
            storage.move(world, facing, step);
            engine.burnFuel(world, blockPosList.size() + drill.fuelBurn(world));

            return true;
        } else {
            return false;
        }
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


    public boolean hasFinishedOperation(World world) {
        return drill.hasFinishedOperation(world);
    }

    public void performOperation(World world, int tick) {
        if (drill != null) {
            this.drill.performOperation(world, tick);
        }
    }
}
