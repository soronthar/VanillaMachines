package com.soronthar.mc.vanillamod;

import com.soronthar.mc.vanillamod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = VanillaMod.MODID, version = VanillaMod.VERSION)
public class VanillaMod {
    public static final String MODID = "vanillamod";
    public static final String VERSION = "0.1";
    public static final Logger log = LogManager.getFormatterLogger(MODID);


    @Mod.Instance(MODID)
    public static VanillaMod instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {


    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onEvent(PlayerInteractEvent event) {

        EntityPlayer entityPlayer = event.entityPlayer;
        World world = event.world;
        BlockPos activatorPos = event.pos;
        if (!world.isRemote) {
            if (activatorPos != null) {
                IBlockState blockState = world.getBlockState(activatorPos);
                Block activatorBlock = blockState.getBlock();
                if (activatorBlock.equals(EngineModule.getActivatorBlock()) && !world.isBlockPowered(activatorPos)) {
                    PoweredConstruct construct= detectPoweredConstruct(world, activatorPos);
                    if (construct!=null) {
                        PoweredConstructEntity entity = new PoweredConstructEntity(construct);
                        world.setTileEntity(activatorPos, entity);
                    }
                }
            }
        }
    }

    public PoweredConstruct detectPoweredConstruct(World world, BlockPos activatorPos) {
        PoweredConstruct construct = null;
        EngineModule engine = detectEngineModule(world, activatorPos);
        if (engine!=null) {
            BlockPos propellerPos = engine.propellerPos;
            RailsModule railsModule = detectRailModule(world, propellerPos);

            if (railsModule != null) {
                construct = new PoweredConstruct(engine,railsModule);
            }

        }
        return construct;
    }

    private RailsModule detectRailModule(World world, BlockPos propellerPos) {
        BlockPos[] rails = new BlockPos[4];
        RailsModule railsModule=null;
        EnumFacing railsFacing = null;

        EnumFacing[] values = {EnumFacing.NORTH, EnumFacing.WEST};
        for (int i1 = 0, valuesLength = values.length; i1 < valuesLength && railsModule == null; i1++) {
            EnumFacing facing = values[i1];
            rails[0] = propellerPos.offset(facing);
            rails[1] = propellerPos.offset(facing.getOpposite());
            if (GeneralUtils.isBlockInPos(world, rails[0], RailsModule.getRailsBlock()) &&
                    GeneralUtils.isBlockInPos(world, rails[1], RailsModule.getRailsBlock())) {
                EnumFacing[] t = {facing.rotateY(), facing.rotateYCCW()};
                for (int i = 0, tLength = t.length; i < tLength && railsModule == null; i++) {
                    EnumFacing enumFacing = t[i];
                    if (GeneralUtils.isBlockInPos(world, rails[0].offset(enumFacing), RailsModule.getRailsBlock()) &&
                            GeneralUtils.isBlockInPos(world, rails[1].offset(enumFacing), RailsModule.getRailsBlock())) {
                        rails[2] = rails[0].offset(enumFacing);
                        rails[3] = rails[1].offset(enumFacing);
                        railsFacing = enumFacing.getOpposite();
                        railsModule=new RailsModule(rails, railsFacing);
                    }
                }
            }
        }
        return railsModule;
    }

    private EngineModule detectEngineModule(World world, BlockPos activatorPos) {
        EngineModule engine=null;

        EnumFacing controllerFacing = GeneralUtils.findBlockAround(world, activatorPos, EngineModule.getControllerBlock());
        if (controllerFacing != null) {
            BlockPos controllerPos = activatorPos.offset(controllerFacing);
            BlockPos propellerPos = controllerPos.down();
            if (GeneralUtils.isBlockInPos(world, propellerPos, EngineModule.getPropellerBlockOff())) {
                engine=new EngineModule(activatorPos, controllerPos, propellerPos);
            }
        }
        return engine;
    }
}
