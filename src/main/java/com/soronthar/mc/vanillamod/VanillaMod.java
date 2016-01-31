package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.commands.SpawnBigDrillerCommand;
import com.soronthar.mc.vanillamod.commands.SpawnSmallDrillerCommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//TODO: remove the validation by using onBlockDestroyed or something
@Mod(modid = VanillaMod.MODID, version = VanillaMod.VERSION, acceptedMinecraftVersions = "1.8.9", acceptableRemoteVersions="*" )
public class VanillaMod {
    public static final String MODID = "vanillamod";
    public static final String VERSION = "0.1";


    @Mod.Instance(MODID)
    public static VanillaMod instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {


    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new SpawnSmallDrillerCommand());
        event.registerServerCommand(new SpawnBigDrillerCommand());
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onEvent(PlayerInteractEvent event) {

        EntityPlayer entityPlayer = event.entityPlayer;
        World world = event.world;
        if (!world.isRemote) {
            BlockPos activatorPos = event.pos;
            if (activatorPos != null) {
                MovingMachine construct = MovingMachine.detectMovingMachine(world, activatorPos);
                if (construct != null) {
                    MovingMachineEntity entity = new MovingMachineEntity(construct);
                    world.setTileEntity(activatorPos, entity);
                }

            }
        }
    }

}
