package com.soronthar.mc.vanillamod;

import net.minecraft.world.World;

public interface Machinery extends Module {

    boolean hasFinishedOperation(World world);

    void performOperation(World world, int tick);

}
