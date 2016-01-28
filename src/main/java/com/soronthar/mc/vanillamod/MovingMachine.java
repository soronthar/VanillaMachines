package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.modules.EngineBlueprint;
import com.soronthar.mc.vanillamod.modules.EngineModule;
import com.soronthar.mc.vanillamod.modules.RailsBlueprint;
import com.soronthar.mc.vanillamod.modules.drill.DrillBlueprint;
import com.soronthar.mc.vanillamod.modules.RailsModule;
import com.soronthar.mc.vanillamod.modules.storage.StorageBlueprint;
import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class MovingMachine {
    World world;
    EngineModule engine;
    RailsModule rails;
    Drill drill;
    public Storage storage;

    List<Harvester> harvester = new ArrayList<>();
    List<Deployer> deployer = new ArrayList<>();

    public MovingMachine() {
    }

    public MovingMachine(World world,EngineModule engine, RailsModule rails) {
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

    public EnumFacing getFacing() {
        return this.rails.facing;
    }

    public static MovingMachine detectMovingMachine(World world, BlockPos activatorPos) {
        MovingMachine machine = null;

        if (EngineBlueprint.isActivatorBlock( world.getBlockState(activatorPos)) && !world.isBlockPowered(activatorPos)) {
            EngineModule engine = EngineBlueprint.detectEngineModule(world, activatorPos);
            if (engine != null) {
                BlockPos propellerPos = engine.propellerPos;
                RailsModule railsModule = RailsBlueprint.detectRailModule(world, propellerPos);

                if (railsModule != null) {
                    machine = new MovingMachine(world,engine, railsModule);
                    Drill drill = DrillBlueprint.detect(world, engine.controllerPos, railsModule.facing);
                    machine.addDrill(drill);

                    Storage storage = StorageBlueprint.detectStorage(world, engine, railsModule.facing);
                    machine.addStorage(storage);
                }
            }
        }
        return machine;
    }


    public boolean isValidStructure() {
        return engine.isValidStructure()
                && rails.isValidStructure();
    }

    public void powerOff() {
        engine.powerOff();
        drill.powerOff();
            IBlockState blockState = world.getBlockState(this.engine.activatorPos);
            if (blockState.getProperties().containsKey(BlockLever.POWERED)) {
                world.setBlockState(this.engine.activatorPos, blockState.withProperty(BlockLever.POWERED, false));
                world.markBlockForUpdate(this.engine.activatorPos);
            }
            world.removeTileEntity(this.engine.activatorPos);
    }

    public boolean canMove(int step, List<BlockPos> blockPosList) {
        boolean canMove = true;
        for (BlockPos blockPos : blockPosList) {
            BlockPos newPos = blockPos.offset(getFacing(), step);
            canMove = canMove && (GeneralUtils.canBlockBeReplaced(world, newPos)
                    || blockPosList.contains(newPos));
        }
        return canMove && this.rails.hasSupport(step, blockPosList);
    }

    public List<BlockPos> getBlockPosList() {
        List<BlockPos> constructBlocks = new ArrayList<BlockPos>();
        constructBlocks.addAll(this.engine.getBlockPosList());
        constructBlocks.addAll(this.rails.getBlockPosList());
        constructBlocks.addAll(this.drill.getBlockPosList());
        constructBlocks.addAll(this.storage.getBlockPosList());
        return constructBlocks;
    }

    public boolean move(int step) {
        List<BlockPos> blockPosList = this.getBlockPosList();
        if (this.isValidStructure()&& this.canMove(step, blockPosList) && engine.hasFuelFor(blockPosList.size())) {
            drill.move(step);
            engine.move(step);
            rails.move(step);
            storage.move(step);
            engine.burnFuel(blockPosList.size());

            return true;
        } else {
            return false;
        }
    }


    public boolean hasFinishedOperation() {
        return drill.hasFinishedOperation();
    }

    public void performOperation(int tick) {
        if (drill != null) {
            this.drill.performOperation(tick);
            engine.burnFuel(this.drill.fuelBurn());
        }
    }

}
