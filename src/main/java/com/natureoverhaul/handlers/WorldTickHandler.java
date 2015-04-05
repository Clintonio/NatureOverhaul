package com.natureoverhaul.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Iterator;
import java.util.Random;

class BlockContainer {
    public Block block;
    int x;
    int y;
    int z;
}

class GrowthHandler {
    private Random random = new Random();

    void processSeedDrops(World world, BlockContainer container) {
        if(container.block instanceof BlockLeavesBase) {
            //world.setBlock(container.x, container.y, container.z, Blocks.air);
        }
    }
}

public class WorldTickHandler {
    private static final int ticksPerSecond = 20;
    private static final int invChunkUpdateChance = 10;
    private GrowthHandler growthHandler = new GrowthHandler();
    private Random random = new Random();

    private BlockContainer getBlockContainer(int x, int y, int z, Block block) {
        BlockContainer container = new BlockContainer();
        container.x = x;
        container.y = y;
        container.z = z;
        container.block = block;

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
                    if(block instanceof Block) {
                        processBlock(world, getBlockContainer(x + xMin, y, z + zMin, block));
                    }
                }
            }
        }
    }

    private void processChunk(World world, ChunkCoordIntPair chunkCoords) {
        if(shouldProcessChunk()) {
            Chunk chunk = world.getChunkFromChunkCoords(chunkCoords.chunkXPos, chunkCoords.chunkZPos);
            if(chunk instanceof Chunk) {
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
