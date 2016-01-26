package com.soronthar.mc.vanillamod.commands;

import com.soronthar.mc.vanillamod.modules.drill.DrillBlueprint;
import com.soronthar.mc.vanillamod.EngineModule;
import com.soronthar.mc.vanillamod.RailsModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;


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

            BlockPos propellerPos = entity.getPosition().offset(facing, 3);
            BlockPos controllerPos = propellerPos.up();
            EnumFacing facingRight = facing.rotateY();
            EnumFacing facingLeft = facing.rotateYCCW();
            EnumFacing facingOpposite = facing.getOpposite();

            world.setBlockState(propellerPos, EngineModule.getPropellerBlockOff().getDefaultState());
            TileEntityFurnace furnace = (TileEntityFurnace) world.getTileEntity(propellerPos);
            furnace.setInventorySlotContents(1, new ItemStack(Items.stick, 64));

            world.setBlockState(controllerPos, EngineModule.getControllerBlock().getDefaultState());
            world.setBlockState(controllerPos.offset(facingOpposite), Blocks.chest.getDefaultState());
            world.setBlockState(controllerPos.up(), EngineModule.getActivatorBlock().getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.forFacings(EnumFacing.UP, facing)));
            world.setBlockState(propellerPos.offset(facingRight), RailsModule.getRailsBlock().getDefaultState());
            world.setBlockState(propellerPos.offset(facingRight).offset(facingOpposite), RailsModule.getRailsBlock().getDefaultState());
            world.setBlockState(propellerPos.offset(facingLeft), RailsModule.getRailsBlock().getDefaultState());
            world.setBlockState(propellerPos.offset(facingLeft).offset(facingOpposite), RailsModule.getRailsBlock().getDefaultState());

            world.setBlockState(controllerPos.offset(facing), DrillBlueprint.getDrillHeadBlock().getDefaultState());

            if (buildTest) {
                BlockPos testAnchor=propellerPos.offset(facing,3).up();
                BlockPos testAnchorLeft=testAnchor.offset(facingRight);
                BlockPos testAnchorRight=testAnchor.offset(facingLeft);

                BlockPos[] pos = calculateTestBlockPosArray(testAnchor, testAnchorLeft, testAnchorRight);

                placeTestBlockArray(world, pos, Blocks.glass,     Blocks.cobblestone, Blocks.iron_block,
                                                Blocks.iron_bars, Blocks.planks,      Blocks.redstone_ore,
                                                Blocks.iron_ore,  Blocks.furnace,     Blocks.piston);

                testAnchor=testAnchor.offset(facing);
                testAnchorLeft=testAnchorLeft.offset(facing);
                testAnchorRight=testAnchorRight.offset(facing);

                pos = calculateTestBlockPosArray(testAnchor, testAnchorLeft, testAnchorRight);

                placeTestBlockArray(world, pos, Blocks.sand, Blocks.sand, Blocks.sand,
                                                Blocks.sand, Blocks.sand, Blocks.gravel,
                                                Blocks.gravel, Blocks.gravel, Blocks.gravel);

            }

        }
    }

    private void placeTestBlockArray(World world, BlockPos[] pos, Block... blocks) {
        for (int i = 0; i < blocks.length; i++) {
            world.setBlockState(pos[i], blocks[i].getDefaultState());
        }
    }

    private BlockPos[] calculateTestBlockPosArray(BlockPos testAnchor, BlockPos testAnchorLeft, BlockPos testAnchorRight) {
        return new BlockPos[]{
                            testAnchor.down(),
                            testAnchorLeft.down(),
                            testAnchorRight.down(),
                            testAnchorLeft,
                            testAnchor,
                            testAnchorRight,
                            testAnchorLeft.up(),
                            testAnchor.up(),
                            testAnchorRight.up(),
                    };
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
