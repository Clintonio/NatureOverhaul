package com.natureoverhaul;

import com.natureoverhaul.handlers.ItemExpireHandler;
import com.natureoverhaul.handlers.ItemThrownHandler;
import com.natureoverhaul.handlers.WorldTickHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;

@Mod(modid = NatureOverhaul.MODID, version = NatureOverhaul.VERSION)
public class NatureOverhaul
{
    public static final String MODID = "natureoverhaul";
    public static final String VERSION = "1.1";

    @Instance(value = NatureOverhaul.MODID)
    public static NatureOverhaul instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {}

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ItemThrownHandler());
        MinecraftForge.EVENT_BUS.register(new ItemExpireHandler());
        FMLCommonHandler.instance().bus().register(new WorldTickHandler());
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {}

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}
}
