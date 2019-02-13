package com.natureoverhaul.handlers;

import net.minecraft.block.BlockFire;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireHandler {
    private int metadataUpdateFlags = 1 + 2; // 1 for block update, 2 for setting on client.

    public void processFire(World world, BlockContainer container) {
        if(container.blockState.getBlock() instanceof BlockFire) {
            // Infinite fire spread by resetting fire to its default state
            world.setBlockState(container.blockPos, container.blockState.getBlock().getDefaultState(), metadataUpdateFlags);
        }
    }
}
