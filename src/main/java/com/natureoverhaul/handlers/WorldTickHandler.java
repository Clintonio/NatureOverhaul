package com.natureoverhaul.handlers;

import com.natureoverhaul.util.XORShiftRandom;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.chunk.Chunk;

import java.util.HashSet;
import java.util.Iterator;

public class WorldTickHandler {
    private static final int ticksPerSecond = 20;
    private static final int invChunkUpdateChance = 10;
    private GrowthHandler growthHandler = new GrowthHandler();
    private FireHandler fireHandler = new FireHandler();
    private XORShiftRandom random = new XORShiftRandom();

    private void processBlock(World world, BlockContainer container) {
        growthHandler.processSeedDrops(world, container);
        fireHandler.processFire(world, container);
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
                        processBlock(world, new BlockContainer(x + xMin, y, z + zMin, block, metadata));
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

    private HashSet getActiveChunkSet(World world) {
        try {
            Class clas = world.getClass().getSuperclass();
            if(world instanceof WorldServerMulti) {
                return (HashSet) ReflectionHelper.findField(clas.getSuperclass(), "activeChunkSet", "field_72993_I").get(world);
            } else if(world instanceof WorldServer) {
                return (HashSet) ReflectionHelper.findField(clas, "activeChunkSet", "field_72993_I").get(world);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return new HashSet();
    }

    private void onTickStart(World world) {}

    private void onTickEnd(World world) {
        if(shouldProcessTick()) {
            Iterator<?> it = getActiveChunkSet(world).iterator();
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
