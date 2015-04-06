package com.natureoverhaul.handlers;

import net.minecraft.block.Block;

class BlockContainer {
    public Block block;
    public int x;
    public int y;
    public int z;
    public int metadata;

    public BlockContainer(int x, int y, int z, Block block, int metadata) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
        this.metadata = metadata;
    }
}