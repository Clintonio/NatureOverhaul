package com.natureoverhaul.handlers;

import com.natureoverhaul.util.XORShiftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockMushroom;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

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
        int x = container.x;
        int y = container.y;
        int z = container.z;
        Block block = container.block;
        if(block instanceof BlockLeaves) {
            return world.canBlockSeeTheSky(x, y + 1, z) && eventHappens(invSeedDropChance);
        } else if(block instanceof IPlantable) {
            return world.canBlockSeeTheSky(x, y + 1, z) && eventHappens(invPlantDropChance);
        } else if(block instanceof BlockMushroom) {
            return world.canBlockSeeTheSky(x, y + 1, z) && eventHappens(invPlantDropChance);
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