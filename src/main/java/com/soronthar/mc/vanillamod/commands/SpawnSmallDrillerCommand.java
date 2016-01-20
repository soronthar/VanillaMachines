package com.soronthar.mc.vanillamod.commands;

import com.soronthar.mc.vanillamod.EngineModule;
import com.soronthar.mc.vanillamod.RailsModule;
import com.soronthar.mc.vanillamod.SmallDrillModule;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRailBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpawnSmallDrillerCommand implements ICommand{
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) {
            boolean buildTest=args.length>0 && args[0].equals("test");

            Entity entity = sender.getCommandSenderEntity();
            EnumFacing facing = entity.getHorizontalFacing();

            BlockPos anchor = entity.getPosition().offset(facing, 3);

            world.setBlockState(anchor, EngineModule.getPropellerBlockOff().getDefaultState());
            TileEntityFurnace furnace = (TileEntityFurnace) world.getTileEntity(anchor);
            furnace.setInventorySlotContents(1,new ItemStack(Items.stick,64));

            world.setBlockState(anchor.up(), EngineModule.getControllerBlock().getDefaultState());
            world.setBlockState(anchor.up().up(), EngineModule.getActivatorBlock().getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.forFacings(EnumFacing.UP, facing)));
            world.setBlockState(anchor.offset(facing.rotateY()), RailsModule.getRailsBlock().getDefaultState());
            world.setBlockState(anchor.offset(facing.rotateY()).offset(facing.getOpposite()), RailsModule.getRailsBlock().getDefaultState());
            world.setBlockState(anchor.offset(facing.rotateYCCW()), RailsModule.getRailsBlock().getDefaultState());
            world.setBlockState(anchor.offset(facing.rotateYCCW()).offset(facing.getOpposite()), RailsModule.getRailsBlock().getDefaultState());

            world.setBlockState(anchor.up().offset(facing), SmallDrillModule.getDrillHeadBlock().getDefaultState());

            if (buildTest) {
                BlockPos testAnchor=anchor.offset(facing,3).up();
                BlockPos testAnchorLeft=testAnchor.offset(facing.rotateY());
                BlockPos testAnchorRight=testAnchor.offset(facing.rotateYCCW());


                world.setBlockState(testAnchor.down(), Blocks.glass.getDefaultState());
                world.setBlockState(testAnchorLeft.down(), Blocks.cobblestone.getDefaultState());
                world.setBlockState(testAnchorRight.down(), Blocks.iron_block.getDefaultState());
                world.setBlockState(testAnchorLeft,Blocks.iron_bars.getDefaultState());
                world.setBlockState(testAnchor,Blocks.planks.getDefaultState());
                world.setBlockState(testAnchorRight, Blocks.redstone_ore.getDefaultState());
                world.setBlockState(testAnchorLeft.up(), Blocks.iron_ore.getDefaultState());
                world.setBlockState(testAnchor.up(), Blocks.furnace.getDefaultState());
                world.setBlockState(testAnchorRight.up(), Blocks.piston.getDefaultState());


                testAnchor=testAnchor.offset(facing);
                testAnchorLeft=testAnchorLeft.offset(facing);
                testAnchorRight=testAnchorRight.offset(facing);

                world.setBlockState(testAnchor.down(), Blocks.sand.getDefaultState());
                world.setBlockState(testAnchorLeft.down(), Blocks.sand.getDefaultState());
                world.setBlockState(testAnchorRight.down(), Blocks.sand.getDefaultState());
                world.setBlockState(testAnchorLeft,Blocks.sand.getDefaultState());
                world.setBlockState(testAnchor,Blocks.sand.getDefaultState());
                world.setBlockState(testAnchorRight, Blocks.gravel.getDefaultState());
                world.setBlockState(testAnchorLeft.up(), Blocks.gravel.getDefaultState());
                world.setBlockState(testAnchor.up(), Blocks.gravel.getDefaultState());
                world.setBlockState(testAnchorRight.up(), Blocks.gravel.getDefaultState());




            }

        }
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("ssd");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "spawnSDrill [test]";
    }

    @Override
    public String getCommandName() {
        return "spawnSDrill";
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
