package com.natureoverhaul.handlers;

import com.natureoverhaul.util.XORShiftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import org.apache.logging.log4j.LogManager;

class GrowthHandler {
    private XORShiftRandom random = new XORShiftRandom();
    private static final int invSeedDropChance = 40000;
    private static final int invPlantDropChance = 40000;

    private boolean eventHappens(int chance) {
        int curInt = random.nextInt();
        int halfChance = Integer.MAX_VALUE / chance;
        return ((curInt >= -halfChance) && (curInt < (halfChance)));
    }

    private boolean shouldDropSeeds(World world, BlockContainer container) {
        BlockPos blockPos = container.blockPos;
        IBlockState blockState = container.blockState;
        Block block = blockState.getBlock();
        if(block instanceof BlockLeaves) {
            return world.canSeeSky(blockPos.add(0, 1, 0)) && eventHappens(invSeedDropChance);
        } else if(block instanceof IPlantable) {
            return world.canSeeSky(blockPos.add(0, 1, 0)) && eventHappens(invPlantDropChance);
        } else {
            return false;
        }
    }

    private void dropSeed(World world, BlockContainer container) {
        BlockPos blockPos = container.blockPos;
        Block block = container.blockState.getBlock();
        Item dropItem = block.getItemDropped(container.blockState, world.rand, 0);
        ItemStack stack = new ItemStack(dropItem, 1, block.damageDropped(container.blockState));
        EntityItem entity = new EntityItem(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
        entity.addVelocity(0, 0.5, 0);
        if (entity.isDead) {
            LogManager.getLogger().warn("Entity created is already dead: " + entity.getItem().getDisplayName());
        } else {
            world.spawnEntity(entity);
        }
    }

    public void processSeedDrops(World world, BlockContainer container) {
        if(shouldDropSeeds(world, container)) {
            dropSeed(world, container);
        }
    }
}
