package com.soronthar.mc.vanillamachines;

public interface Machinery extends Module {

    boolean hasFinishedOperation();

    void performOperation(int tick);

}
