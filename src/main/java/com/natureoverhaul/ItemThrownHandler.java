package com.natureoverhaul;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.*;
import net.minecraftforge.event.entity.item.ItemTossEvent;

public class ItemThrownHandler {
    @SubscribeEvent
    public void onItemTossed(ItemTossEvent event) {
        ItemStack stack = event.entityItem.getEntityItem();
        Item item = stack.getItem();
        if((item instanceof ItemSeeds) || (item instanceof ItemSeedFood) || (item instanceof ItemBlock)) {
            event.entityItem.age = event.entityItem.lifespan - 10;
        }
    }
}
