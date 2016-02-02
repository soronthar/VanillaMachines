package com.soronthar.mc.vanillamachines;

import com.soronthar.mc.vanillamachines.util.LogHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

//TODO: chunk borders... stop if chunk is not loaded.
//TODO: When the world is closed, the furnace keeps "burning"
//TODO: Persist the entity..somehow.
//TODO: Turn the machine with  a stick, or using other mechanism like the sign or a block (piston)
//TODO: Change System.out to logger
public class MovingMachineEntity extends TileEntity implements ITickable {
    static volatile int instanceCount=0;

    MovingMachine movingMachine;
    int tick;

    public MovingMachineEntity(MovingMachine movingMachine) {
        this.movingMachine = movingMachine;
        instanceCount++;
        LogHelper.debug("Creating " + this + ". Instance Count: " + instanceCount);

    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.movingMachine=null;
    }

    @Override
    public void update() {
        World world = getWorld();
        if (!world.isRemote ) {
            this.tick++;
            if (!this.isInvalid() && movingMachine != null && this.tick % 20 == 0) {
                BlockPos activatorPos = movingMachine.engine.activatorPos;
                if (movingMachine.isValidStructure() && world.isBlockPowered(activatorPos)) {
                    if (!movingMachine.engine.isPowered()) {
                            movingMachine.engine.powerOn();
                    }
                    if (!movingMachine.hasFinishedOperation()) {
                        movingMachine.performOperation(this.tick);
                    } else {
                        move(activatorPos);
                    }
                } else {
                    movingMachine.powerOff();
                    this.invalidate();
                }
            }
        }
    }

    private void move(BlockPos activatorPos) {
        getWorld().removeTileEntity(activatorPos);
        boolean isMoving = movingMachine.move(1);
        if (isMoving) {
            getWorld().setTileEntity(movingMachine.engine.activatorPos, this);
        } else {
            movingMachine.powerOff();
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        instanceCount--;
        LogHelper.debug("Collecting " + this + ". Instance Count: " + instanceCount);
    }


}
