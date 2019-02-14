package com.natureoverhaul;

import com.natureoverhaul.handlers.ItemExpireHandler;
import com.natureoverhaul.handlers.ItemThrownHandler;
import com.natureoverhaul.handlers.WorldTickHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = NatureOverhaul.MODID, version = NatureOverhaul.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class NatureOverhaul {
    public static final String MODID = "natureoverhaul";
    public static final String VERSION = "1.3";

    @Mod.Instance(value = NatureOverhaul.MODID)
    public static NatureOverhaul instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ItemThrownHandler());
        MinecraftForge.EVENT_BUS.register(new ItemExpireHandler());
        FMLCommonHandler.instance().bus().register(new WorldTickHandler());
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
