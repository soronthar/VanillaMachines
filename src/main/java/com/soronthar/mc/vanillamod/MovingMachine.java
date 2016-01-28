package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.modules.EngineModule;
import com.soronthar.mc.vanillamod.modules.drill.DrillBlueprint;
import com.soronthar.mc.vanillamod.modules.RailsBlueprint;
import com.soronthar.mc.vanillamod.modules.storage.StorageBlueprint;
import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class MovingMachine {
    World world;
    EngineModule engine;
    RailsBlueprint rails;
    Drill drill;
    public Storage storage;

    List<Harvester> harvester = new ArrayList<>();
    List<Deployer> deployer = new ArrayList<>();

    public MovingMachine() {
    }

    public MovingMachine(World world,EngineModule engine, RailsBlueprint rails) {
        this.world=world;
        this.engine = engine;
        this.rails = rails;
        this.engine.setMachine(this);
        this.rails.setMachine(this);
    }

    public void addDrill(Drill drill) {
        this.drill = drill;
        this.drill.setMachine(this);
    }

    private void addStorage(Storage storage) {
        this.storage = storage;
        this.storage.setMachine(this);
    }

    public boolean hasStorage() {
        return this.storage!=null;
    }

    public World getWorld() {
        return world;
    }

    public static MovingMachine detectMovingMachine(World world, BlockPos activatorPos) {
        MovingMachine construct = null;
        IBlockState blockState = world.getBlockState(activatorPos);
        Block activatorBlock = blockState.getBlock();

        if (activatorBlock.equals(EngineModule.getActivatorBlock()) && !world.isBlockPowered(activatorPos)) {
            EngineModule engine = EngineModule.detectEngineModule(world, activatorPos);
            if (engine != null) {
                BlockPos propellerPos = engine.propellerPos;
                RailsBlueprint railsBlueprint = RailsBlueprint.detectRailModule(world, propellerPos);

                if (railsBlueprint != null) {
                    construct = new MovingMachine(world,engine, railsBlueprint);
                    Drill drill = DrillBlueprint.detect(world, engine.controllerPos, railsBlueprint.facing);
                    construct.addDrill(drill);

                    Storage storage = StorageBlueprint.detectStorage(world, engine, railsBlueprint.facing);
                    construct.addStorage(storage);
                }
            }
        }
        return construct;
    }


    public boolean isValidStructure() {
        return engine.isValidStructure()
                && rails.isValidStructure();
    }

    public void powerOff(World world) {
        engine.powerOff();
        drill.powerOff(world);
            IBlockState blockState = world.getBlockState(this.engine.activatorPos);
            if (blockState.getProperties().containsKey(BlockLever.POWERED)) {
                world.setBlockState(this.engine.activatorPos, blockState.withProperty(BlockLever.POWERED, false));
                world.markBlockForUpdate(this.engine.activatorPos);
            }
            world.removeTileEntity(this.engine.activatorPos);
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
        constructBlocks.addAll(this.drill.getBlockPosList());
        constructBlocks.addAll(this.storage.getBlockPosList());
        return constructBlocks;
    }

    public boolean move(World world, int step) {
        EnumFacing facing = rails.facing;
        List<BlockPos> blockPosList = this.getBlockPosList();
        if (this.isValidStructure()&& this.canMove(world, facing, step, blockPosList) && engine.hasFuelFor(blockPosList.size())) {
            drill.move(world, facing, step);
            engine.move(world, facing, step);
            rails.move(world, facing, step);
            storage.move(world, facing, step);
            engine.burnFuel(blockPosList.size());

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
            engine.burnFuel(this.drill.fuelBurn(world));
        }
    }

}
