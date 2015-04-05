package com.natureoverhaul;

import com.natureoverhaul.handlers.ItemExpireHandler;
import com.natureoverhaul.handlers.ItemThrownHandler;
import com.natureoverhaul.handlers.WorldTickHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "natureoverhaul";
    public static final String VERSION = "1.1";

    @Instance(value = ExampleMod.MODID)
    public static ExampleMod instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {}

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ItemThrownHandler());
        MinecraftForge.EVENT_BUS.register(new ItemExpireHandler());
        FMLCommonHandler.instance().bus().register(new WorldTickHandler());
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
