package com.soronthar.mc.vanillamod;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

//TODO: chunk borders... stop if chunk is not loaded.
//TODO: When the world is closed, the furnace keeps "burning"
//TODO: Persist the entity..somehow.
//TODO: the construct should have a reference to the world.
//TODO: Turn the machine with  a stick, or using other mechanism like the sign or a block (piston)
public class MovingMachineEntity extends TileEntity implements ITickable {
    MovingMachine movingMachine;
    int tick;

    public MovingMachineEntity(MovingMachine movingMachine) {
        this.movingMachine = movingMachine;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.movingMachine=null;
    }

    @Override
    public void update() {
        this.tick++;
        World world = getWorld();
        if (!world.isRemote && !this.isInvalid() && movingMachine != null && this.tick % 20 == 0) {
            BlockPos activatorPos = movingMachine.engine.activatorPos;
            if (movingMachine.isValidStructure() && world.isBlockPowered(activatorPos)) {
                if (!movingMachine.engine.isPowered()) {
                        movingMachine.engine.powerOn();
                }

                if (!movingMachine.hasFinishedOperation(world)) {
                    movingMachine.performOperation(world,this.tick);
                } else {
                    move(activatorPos);
                }
            } else {
                movingMachine.powerOff(getWorld());
                this.invalidate();
            }
        }
    }

    private void move(BlockPos activatorPos) {
        getWorld().removeTileEntity(activatorPos);
        boolean isMoving = movingMachine.move(getWorld(), 1);
        if (isMoving) {
            getWorld().setTileEntity(movingMachine.engine.activatorPos, this);
        } else {
            movingMachine.powerOff(getWorld());
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Collecting " + this);
    }


}
