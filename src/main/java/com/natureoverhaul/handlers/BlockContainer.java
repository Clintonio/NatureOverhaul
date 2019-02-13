package com.natureoverhaul.handlers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

class BlockContainer {
    public IBlockState blockState;
    public BlockPos blockPos;

    public BlockContainer(BlockPos blockPos, IBlockState blockState) {
        this.blockPos = blockPos;
        this.blockState = blockState;
    }
}
