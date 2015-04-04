package com.natureoverhaul;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

import java.lang.reflect.Field;

public class ItemExpireHandler {
    private int blockPlaceFlags = 1 + 2; // 1 for block update, 2 for setting on client.

    private int getItemMetadata(EntityItem entityItem) {
        // This may not be true for all items, but is definitely true for item blocks.
        ItemStack stack = entityItem.getEntityItem();
        return stack.getItemDamage();
    }

    private Block getBlock(IPlantable plantable) throws NoSuchFieldException, IllegalAccessException {
        Class<?> c = plantable.getClass();

        Field field;
        if(plantable instanceof ItemSeeds) {
            field = c.getDeclaredField("field_150925_a");
        } else if(plantable instanceof ItemSeedFood) {
            field = c.getDeclaredField("field_150908_b");
        } else if(plantable instanceof Block) {
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

        if(block.canPlaceBlockAt(world, x, y, z)) {
            world.setBlock(x, y, z, block, metadata, blockPlaceFlags);
            block.onBlockPlacedBy(world, x, y, z, new StubEntityLiving(world), entity.getEntityItem());
        }
    }

    private <T extends IPlantable> void plant(ItemExpireEvent event, T plantable) {
        EntityItem entity = event.entityItem;
        int x = (int) Math.floor(entity.posX);
        int y = (int) Math.floor(entity.posY) - 1;
        int z = (int) Math.floor(entity.posZ);
        int dimension = entity.dimension;

        World world = MinecraftServer.getServer().worldServerForDimension(dimension);
        if (world.getBlock(x, y, z).canSustainPlant(world, x, y, z, ForgeDirection.UP, plantable)
                && world.isAirBlock(x, y + 1, z)) {
            try {
                place(world, entity, getBlock(plantable));
            } catch (NoSuchFieldException | IllegalAccessException | RuntimeException e) {
                System.err.print(e);
            }
        }
    }

    @SubscribeEvent
    public void onItemTossed(ItemExpireEvent event) {
        ItemStack stack = event.entityItem.getEntityItem();
        Item item = stack.getItem();
        if(item instanceof ItemSeeds) {
            ItemSeeds seeds = (ItemSeeds) item;
            plant(event, seeds);
        } else if(item instanceof ItemSeedFood) {
            ItemSeedFood seedFood = (ItemSeedFood) item;
            plant(event, seedFood);
        } else if(item instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) item;
            Block block = itemBlock.field_150939_a;
            if(block instanceof IPlantable) {
                plant(event, (IPlantable) block);
            }
        }
    }
}
