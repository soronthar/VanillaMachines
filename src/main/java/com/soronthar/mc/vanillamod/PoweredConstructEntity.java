package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import com.sun.xml.internal.ws.api.pipe.Engine;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;

//TODOÑ chunk borders... stop if chunk is not loaded.
//TODO: When the world is closed, the furnace keeps "burning"
//TODO: Persist the entity..somehow.
//TODO: the construct should have a reference to the world.
public class PoweredConstructEntity extends TileEntity implements ITickable {

    PoweredConstruct poweredConstruct;
    int tick;

    public PoweredConstructEntity(PoweredConstruct poweredConstruct) {
        this.poweredConstruct = poweredConstruct;
    }

    @Override
    public void update() {
        poweredConstruct.engine.validateFuel(getWorld());
        this.tick++;
        if (!getWorld().isRemote && !this.isInvalid() && poweredConstruct != null && this.tick % 20 == 0) {
            BlockPos activatorPos = poweredConstruct.engine.activatorPos;
            if (poweredConstruct.isValidStructure(getWorld()) && getWorld().isBlockPowered(activatorPos)) {
                if (!GeneralUtils.isBlockInPos(getWorld(), poweredConstruct.engine.propellerPos, EngineModule.getPropellerBlockOn())) {
                    poweredConstruct.engine.powerOn(getWorld());
                }
                getWorld().removeTileEntity(activatorPos);
                boolean isMoving = poweredConstruct.move(getWorld(), 1);
                if (isMoving) {
                    getWorld().setTileEntity(poweredConstruct.engine.activatorPos, this);
                } else {
                    powerOff(activatorPos);
                }
            } else {
                powerOff(activatorPos);
            }
        }
    }

    private void powerOff(BlockPos leverPos) {
        poweredConstruct.powerOff(getWorld());
        getWorld().removeTileEntity(leverPos);
        IBlockState blockState = getWorld().getBlockState(leverPos);
        if (blockState.getProperties().containsKey(BlockLever.POWERED)) {
            getWorld().setBlockState(leverPos, blockState.withProperty(BlockLever.POWERED, false));
            getWorld().markBlockForUpdate(leverPos);
        }
        this.invalidate();
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        poweredConstruct.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        poweredConstruct.writeToNBT(compound);

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Collecting " + this);
    }


}
