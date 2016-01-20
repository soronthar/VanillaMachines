package com.soronthar.mc.vanillamod;

import net.minecraft.world.World;

public interface Powered {
    void powerOff(World world);

    int fuelBurn(World world);


}
