package com.soronthar.mc.vanillamod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
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


    public void move(World world, int step) {
        if (this.isValidStructure(world)) {
            engine.activatorPos = moveBlock(world, engine.activatorPos, step);
            engine.controllerPos = moveBlock(world, engine.controllerPos, step);
            engine.propellerPos = moveBlock(world, engine.propellerPos, step);
            rails.rails[0] = moveBlock(world, rails.rails[0], step);
            rails.rails[1] = moveBlock(world, rails.rails[1], step);
            rails.rails[2] = moveBlock(world, rails.rails[2], step);
            rails.rails[3] = moveBlock(world, rails.rails[3], step);
        }
    }

    @Override
    public boolean isValidStructure(World world) {
        return engine.isValidStructure(world)
                && rails.isValidStructure(world);
    }

    private BlockPos moveBlock(World world, BlockPos pos, int step) {
        IBlockState state = world.getBlockState(pos);
        world.setBlockToAir(pos);
        world.markBlockForUpdate(pos);
        BlockPos newPos = pos.offset(rails.facing, step);
        world.setBlockState(newPos, state);
        world.markBlockForUpdate(newPos);
        return newPos;
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
