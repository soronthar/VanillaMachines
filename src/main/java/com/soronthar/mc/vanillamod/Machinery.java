package com.soronthar.mc.vanillamod;

import net.minecraft.world.World;

public interface Machinery extends Construct{

    boolean hasFinishedOperation(World world);

    void performOperation(World world);

}
