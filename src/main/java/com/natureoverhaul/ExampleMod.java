package com.natureoverhaul;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "natureoverhaul";
    public static final String VERSION = "1.0";

    @Instance(value = ExampleMod.MODID)
    public static ExampleMod instance;

    @SidedProxy(clientSide="com.natureoverhaul.client.ClientProxy", serverSide="com.natureoverhaul.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {}

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ItemThrownHandler());
        MinecraftForge.EVENT_BUS.register(new ItemExpireHandler());
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.registerRenderers();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
