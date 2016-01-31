package com.soronthar.mc.vanillamod.util;

import com.soronthar.mc.vanillamod.VanillaMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class LogHelper {
    private static final Logger log = LogManager.getFormatterLogger(VanillaMod.MODID);
    private static final boolean IS_DEBUG_ENABLED=true;

    //Workaround of forge having the logger lever at info, with no way to change it.
    public static void debug(String message) {
        if (IS_DEBUG_ENABLED) {
            log.info(message);
        }
    }
}
