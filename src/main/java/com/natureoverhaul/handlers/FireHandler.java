package com.natureoverhaul.handlers;

import net.minecraft.block.BlockFire;
import net.minecraft.world.World;

public class FireHandler {
    private int metadataUpdateFlags = 1 + 2; // 1 for block update, 2 for setting on client.

    public void processFire(World world, BlockContainer container) {
        if(container.block instanceof BlockFire) {
            world.setBlockMetadataWithNotify(container.x, container.y, container.z, 0, metadataUpdateFlags);
        }
    }
}
