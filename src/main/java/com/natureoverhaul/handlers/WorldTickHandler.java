package com.natureoverhaul.handlers;

import com.natureoverhaul.util.XORShiftRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.Collection;

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

    private boolean shouldProcessChunk() {
        return random.nextInt(invChunkUpdateChance) == 0;
    }

    private boolean shouldProcessTick() {
        return random.nextInt(ticksPerSecond) == 0;
    }

    private Collection<Chunk> getActiveChunkSet(World world) {
        return ((ChunkProviderServer) world.getChunkProvider()).getLoadedChunks();
    }

    private void onTickStart(World world) {
    }

    private void onTickEnd(World world) {
        if (shouldProcessTick() && shouldProcessWorld(world)) {
            for (Chunk chunk : getActiveChunkSet(world)) {
                if (shouldProcessChunk()) {
                    processChunk(world, chunk);
                }
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
