package com.natureoverhaul.handlers;

import com.natureoverhaul.util.XORShiftRandom;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Iterator;

class BlockContainer {
    public Block block;
    public int x;
    public int y;
    public int z;
    public int metadata;
}

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
        EntityItem entity = new EntityItem(world, container.x, container.y, container.z, new ItemStack(dropItem));
        entity.addVelocity(0, 0.5, 0);
        world.spawnEntityInWorld(entity);
    }

    public void processSeedDrops(World world, BlockContainer container) {
        if(shouldDropSeeds(world, container)) {
            dropSeed(world, container);
        }
    }
}

public class WorldTickHandler {
    private static final int ticksPerSecond = 20;
    private static final int invChunkUpdateChance = 10;
    private GrowthHandler growthHandler = new GrowthHandler();
    private XORShiftRandom random = new XORShiftRandom();

    private BlockContainer getBlockContainer(int x, int y, int z, Block block, int metadata) {
        BlockContainer container = new BlockContainer();
        container.x = x;
        container.y = y;
        container.z = z;
        container.block = block;
        container.metadata = metadata;

        return container;
    }

    private void processBlock(World world, BlockContainer container) {
        growthHandler.processSeedDrops(world, container);
    }

    private void processChunk(World world, Chunk chunk) {
        int height = world.getHeight();
        int xMin = chunk.xPosition * 16;
        int zMin = chunk.zPosition * 16;
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                for(int y = 0; y < height; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    int metadata = chunk.getBlockMetadata(x, y, z);
                    if(block instanceof Block) {
                        processBlock(world, getBlockContainer(x + xMin, y, z + zMin, block, metadata));
                    }
                }
            }
        }
    }

    private void processChunk(World world, ChunkCoordIntPair chunkCoords) {
        if(shouldProcessChunk()) {
            Chunk chunk = world.getChunkFromChunkCoords(chunkCoords.chunkXPos, chunkCoords.chunkZPos);
            if((chunk instanceof Chunk) && chunk.isChunkLoaded && chunk.isTerrainPopulated) {
                processChunk(world, chunk);
            }
        }
    }

    private boolean shouldProcessChunk() {
        return random.nextInt(invChunkUpdateChance) == 0;
    }

    private boolean shouldProcessTick() {
        return random.nextInt(ticksPerSecond) == 0;
    }

    private void onTickStart(World world) {}

    private void onTickEnd(World world) {
        if(shouldProcessTick()) {
            Iterator<?> it = world.activeChunkSet.iterator();
            while(it.hasNext()) {
                ChunkCoordIntPair chunkCoords = (ChunkCoordIntPair) it.next();
                processChunk(world, chunkCoords);
            }
        }
    }

    @SubscribeEvent
    public void tickStart(TickEvent.WorldTickEvent event) {
        if(event.side.isServer()) {
            if(event.phase == TickEvent.Phase.START) {
                onTickStart(event.world);
            } else if(event.phase == TickEvent.Phase.END) {
                onTickEnd(event.world);
            }
        }
    }
}
