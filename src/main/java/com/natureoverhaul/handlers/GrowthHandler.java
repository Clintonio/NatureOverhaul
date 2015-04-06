package com.natureoverhaul.handlers;

import com.natureoverhaul.util.XORShiftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class GrowthHandler {
    private XORShiftRandom random = new XORShiftRandom();
    private static final int invSeedDropChance = 10000;

    private boolean eventHappens(int chance) {
        int curInt = random.nextInt();
        int halfChance = Integer.MAX_VALUE / chance;
        return ((curInt >= -halfChance) && (curInt < (halfChance)));
    }

    private boolean shouldDropSeeds(World world, BlockContainer container) {
        Block block = container.block;
        if(block instanceof BlockLeaves) {
            return world.canBlockSeeTheSky(container.x, container.y + 1, container.z) && eventHappens(invSeedDropChance);
        } else {
            return false;
        }
    }

    private void dropSeed(World world, BlockContainer container) {
        Item dropItem = container.block.getItemDropped(container.metadata, world.rand, 0);
        ItemStack stack = new ItemStack(dropItem, 1, container.block.damageDropped(container.metadata));
        EntityItem entity = new EntityItem(world, container.x, container.y, container.z, stack);
        entity.addVelocity(0, 0.5, 0);
        world.spawnEntityInWorld(entity);
    }

    public void processSeedDrops(World world, BlockContainer container) {
        if(shouldDropSeeds(world, container)) {
            dropSeed(world, container);
        }
    }
}