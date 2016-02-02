package com.soronthar.mc.vanillamachines.modules.drill;

import com.soronthar.mc.vanillamachines.Drill;
import com.soronthar.mc.vanillamachines.util.GeneralUtils;
import com.soronthar.mc.vanillamachines.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DrillBlueprint {
    public static Drill detect(World world, BlockPos controllerPos, EnumFacing facing) {
        BlockPos drillHeadPos = controllerPos.offset(facing);
        if (GeneralUtils.isBlockInPos(world, drillHeadPos, getDrillHeadBlock())) {
            if (!GeneralUtils.isBlockInPos(world, drillHeadPos.offset(facing), getDrillHeadBlock())) {
                return new SmallDrillModule(drillHeadPos, facing);
            } else {
                Map<BlockPos, Block> blockPosBlockMap = calculateBlockPosForDrill(drillHeadPos, facing);
                boolean isValid=true;
                for (Iterator<Map.Entry<BlockPos, Block>> iterator = blockPosBlockMap.entrySet().iterator() ; iterator.hasNext() && isValid; ) {
                    Map.Entry<BlockPos, Block> entry = iterator.next();
                    isValid = isValid && GeneralUtils.isBlockInPos(world, entry.getKey(), entry.getValue());
                }
                System.out.println("blockPosBlockMap = " + blockPosBlockMap);
                if (isValid) {
                    LogHelper.debug("Big Drill Detected!");
                }
                return new BigDrillModule(drillHeadPos,facing);
            }
        } else {
            return NopDrill.instance;
        }
    }

    //TODO: Optimize
    public static Map<BlockPos,Block> calculateBlockPosForDrill(BlockPos anchor, EnumFacing facing) {
        Map<BlockPos,Block> blocks=new HashMap<BlockPos, Block>();

        BlockPos level1=anchor;
        EnumFacing left = facing.rotateYCCW();
        EnumFacing right = facing.rotateY();

        firstSlice(blocks, level1);

        level1=level1.offset(facing);
        secondSlice(blocks, level1);

        level1=level1.offset(facing);
        thirdSlice(blocks, level1, left, right);

        level1=level1.offset(facing);
        forthSlice(blocks, level1, left, right);

        return blocks;
    }

    private static void forthSlice(Map<BlockPos, Block> blocks, BlockPos level1, EnumFacing left, EnumFacing right) {
        blocks.put(level1,getDrillHeadBlock());
        BlockPos level2 = level1.up();
        blocks.put(level2,getDrillHeadBlock());
        blocks.put(level2.up(), getDrillHeadBlock());
        blocks.put(level2.offset(left),getDrillHeadBlock());
        blocks.put(level2.offset(right),getDrillHeadBlock());
    }

    private static void thirdSlice(Map<BlockPos, Block> blocks, BlockPos level1, EnumFacing left, EnumFacing right) {
        blocks.put(level1.down(), Blocks.nether_brick_fence);

        blocks.put(level1,Blocks.nether_brick_fence);
        blocks.put(level1.offset(left),Blocks.nether_brick_fence);
        blocks.put(level1.offset(right),Blocks.nether_brick_fence);


        BlockPos level2 = level1.up();
        blocks.put(level2,Blocks.diamond_block);
        blocks.put(level2.offset(left),Blocks.nether_brick_fence);
        blocks.put(level2.offset(left).offset(left),Blocks.nether_brick_fence);
        blocks.put(level2.offset(right),Blocks.nether_brick_fence);
        blocks.put(level2.offset(right).offset(right),Blocks.nether_brick_fence);


        BlockPos level3 = level2.up();
        blocks.put(level3,Blocks.nether_brick_fence);
        blocks.put(level3.offset(left),Blocks.nether_brick_fence);
        blocks.put(level3.offset(right),Blocks.nether_brick_fence);

        blocks.put(level3.up(),Blocks.nether_brick_fence);
    }

    private static void secondSlice(Map<BlockPos, Block> blocks, BlockPos level1) {
        blocks.put(level1,getDrillHeadBlock());
        blocks.put(level1.up(), Blocks.diamond_block);
    }

    private static void firstSlice(Map<BlockPos, Block> blocks, BlockPos level1) {
        blocks.put(level1,getDrillHeadBlock());
        blocks.put(level1.up(), Blocks.diamond_block);
    }


    public static Block getDrillHeadBlock() {
        return Blocks.iron_block;
    }
}
