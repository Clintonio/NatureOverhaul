package com.natureoverhaul.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.World;

public class WorldTickHandler {

    @SubscribeEvent
    public void tickStart(TickEvent.WorldTickEvent event) {
        if(event.side.isServer()) {
            if(event.phase == TickEvent.Phase.START) {
                onTickStart(event.world);
            } else if(event.phase == TickEvent.Phase.END) {
                onTickEnd(event.world);
            }
        }
    }

    private void onTickStart(World world) {

    }

    private void onTickEnd(World world) {

    }
}
