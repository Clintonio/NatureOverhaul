package com.natureoverhaul.handlers;

import com.natureoverhaul.util.XORShiftRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.HashSet;

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
        int xMin = chunk.x * 16;
        int zMin = chunk.z * 16;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < height; y++) {
                    processBlock(world, new BlockContainer(new BlockPos(x + xMin, y, z + zMin), chunk.getBlockState(x, y, z)));
                }
            }
        }
    }

    private void processChunk(World world, ChunkPos chunkCoords) {
        if (shouldProcessChunk()) {
            Chunk chunk = world.getChunkFromChunkCoords(chunkCoords.x, chunkCoords.z);
            if (chunk.isLoaded() && chunk.isTerrainPopulated()) {
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
            if (world instanceof WorldServerMulti) {
                return (HashSet) ReflectionHelper.findField(clas.getSuperclass(), "activeChunkSet", "field_72993_I").get(world);
            } else if (world instanceof WorldServer) {
                return (HashSet) ReflectionHelper.findField(clas, "activeChunkSet", "field_72993_I").get(world);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return new HashSet();
    }

    private void onTickStart(World world) {
    }

    private void onTickEnd(World world) {
        if (shouldProcessTick() && shouldProcessWorld(world)) {
            for (Object o : getActiveChunkSet(world)) {
                ChunkPos chunkCoords = (ChunkPos) o;
                processChunk(world, chunkCoords);
            }
        }
    }

    private boolean shouldProcessWorld(World world) {
        // This mod only runs in the overworld
        return (world.provider.getDimension() == 0);
    }

    @SubscribeEvent
    public void tickStart(TickEvent.WorldTickEvent event) {
        if (event.side.isServer()) {
            if (event.phase == TickEvent.Phase.START) {
                onTickStart(event.world);
            } else if (event.phase == TickEvent.Phase.END) {
                onTickEnd(event.world);
            }
        }
    }
}
