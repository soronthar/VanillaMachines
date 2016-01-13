package com.soronthar.mc.vanillamod;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;

public class PoweredConstructEntity extends TileEntity implements ITickable {

    PoweredConstruct poweredConstruct;
    int tick;

    public PoweredConstructEntity(PoweredConstruct poweredConstruct) {
        this.poweredConstruct = poweredConstruct;
    }

    @Override
    public void update() {
        this.tick++;
        if (!getWorld().isRemote && !this.isInvalid() && poweredConstruct!=null && this.tick%40==0) {
            BlockPos leverPos = poweredConstruct.engine.activatorPos;

            if (poweredConstruct.isValidStructure(getWorld())) {
                if(Blocks.lever.equals(worldObj.getBlockState(leverPos).getBlock())) {
                    if (!getWorld().isBlockPowered(leverPos)) {
                        powerOff(leverPos);
                    } else {
                        getWorld().removeTileEntity(leverPos);
                        poweredConstruct.move(getWorld(), 1);
                        getWorld().setTileEntity(poweredConstruct.engine.activatorPos,this);

                    }
                }
            } else {
                powerOff(leverPos);
            }
        }
    }

    private void powerOff(BlockPos leverPos) {
        System.out.println("Powering down @ " + this.pos + " - " + this);
        getWorld().removeTileEntity(leverPos);
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
