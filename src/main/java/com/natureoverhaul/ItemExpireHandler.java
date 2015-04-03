package com.natureoverhaul;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraft.item.ItemSeeds;

import java.lang.reflect.Field;

public class ItemExpireHandler {
    // Seeds ID: 295
    private Block getSeedBlock(ItemSeeds seeds) throws NoSuchFieldException, IllegalAccessException {
        Class<?> c = seeds.getClass();
        Field field = c.getDeclaredField("field_150925_a");
        field.setAccessible(true);
        Object obj = field.get(seeds);
        return (Block) obj;
    }

    private void plantSeeds(ItemExpireEvent event, ItemSeeds seeds) {
        EntityItem entity = event.entityItem;
        int x = (int) Math.floor(entity.posX);
        int y = (int) Math.floor(entity.posY) - 1;
        int z = (int) Math.floor(entity.posZ);
        int dimension = entity.dimension;

        World world = MinecraftServer.getServer().worldServerForDimension(dimension);
        if (world.getBlock(x, y, z).canSustainPlant(world, x, y, z, ForgeDirection.UP, seeds)
                && world.isAirBlock(x, y + 1, z)) {
            try {
                world.setBlock(x, y + 1, z, getSeedBlock(seeds));
            } catch (NoSuchFieldException|IllegalAccessException e) {
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
            plantSeeds(event, seeds);
        }
    }
}
