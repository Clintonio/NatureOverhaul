package com.natureoverhaul.handlers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;

public class ItemExpireHandler {
    private int blockPlaceFlags = 1 + 2; // 1 for block update, 2 for setting on client.

    private int getItemMetadata(EntityItem entityItem) {
        // This may not be true for all items, but is definitely true for item blocks.
        ItemStack stack = entityItem.getItem();
        return stack.getMetadata();
    }

    private Block getBlock(IPlantable plantable) throws NoSuchFieldException, IllegalAccessException {
        Class<?> c = plantable.getClass();

        Field field;
        if (plantable instanceof ItemSeeds) {
            field = c.getDeclaredField("crops");
        } else if (plantable instanceof ItemSeedFood) {
            field = c.getDeclaredField("crops");
        } else if (plantable instanceof Block) {
            return (Block) plantable;
        } else {
            throw new RuntimeException("Unknown plantable type");
        }

        field.setAccessible(true);
        Object obj = field.get(plantable);
        return (Block) obj;
    }

    private void place(World world, EntityItem entity, Block block) {
        int metadata = getItemMetadata(entity);
        int x = (int) Math.floor(entity.posX);
        int y = (int) Math.floor(entity.posY);
        int z = (int) Math.floor(entity.posZ);
        BlockPos blockPos = new BlockPos(x, y, z);
        EntityLivingBase placer = new StubEntityLiving(world);

        if (block.canPlaceBlockAt(world, blockPos)) {
            IBlockState blockState = block.getStateForPlacement(world, blockPos, EnumFacing.UP, 0, 0, 0, metadata, placer, EnumHand.MAIN_HAND);
            world.setBlockState(blockPos, blockState, blockPlaceFlags);
            block.onBlockPlacedBy(world, blockPos, blockState, placer, entity.getItem());
        }
    }

    private <T extends IPlantable> void plant(ItemExpireEvent event, T plantable) {
        EntityItem entity = event.getEntityItem();
        int x = (int) Math.floor(entity.posX);
        int y = (int) Math.floor(entity.posY) - 1;
        int z = (int) Math.floor(entity.posZ);
        BlockPos blockPos = new BlockPos(x, y, z);
        World world = entity.world;
        IBlockState blockState = world.getBlockState(blockPos);

        if (blockState.getBlock().canSustainPlant(blockState, world, blockPos, EnumFacing.UP, plantable)
                && world.isAirBlock(blockPos.add(0, 1, 0))) {
            try {
                place(world, entity, getBlock(plantable));
            } catch (Exception e) {
                System.err.print(e);
            }
        }
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        ItemStack stack = event.getEntityItem().getItem();
        Item item = stack.getItem();
        if (item instanceof ItemSeeds) {
            ItemSeeds seeds = (ItemSeeds) item;
            plant(event, seeds);
        } else if (item instanceof ItemSeedFood) {
            ItemSeedFood seedFood = (ItemSeedFood) item;
            plant(event, seedFood);
        } else if (item instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) item;
            Block block = itemBlock.getBlock();
            if (block instanceof IPlantable) {
                plant(event, (IPlantable) block);
            }
        }
    }
}
