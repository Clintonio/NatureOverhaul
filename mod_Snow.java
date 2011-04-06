package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import net.minecraft.client.Minecraft;
import net.minecraft.src.modoptionsapi.*;
import org.lwjgl.input.Keyboard;

public class mod_Snow extends BaseMod
{
	
	public static ModMappedMultiOption SnowMode;
	public static ModSliderOption SnowRate;
	public static ModSliderOption SnowPerTick;
	//public static boolean Mayhem = PReader.PropBoolean("/SnowMod.properties", "Mayhem Mode", "False", "# True = enabled, False = disabled\n");
	//public static int SnowLayers = PReader.PropInt("/SnowMod.properties", "Snow Layers", "5", "# Number of snow blocks that should be placed on top of each other in mayhem mode (Max = 15, Default = 5)\n");
	
	public static double Offset;
	
    public mod_Snow()
    {
		ModOptions mo = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME);
		ModOptions climate = mo.getSubOption(mod_AutoForest.CLIMATE_MENU_NAME);
		
		Integer keys[]  = {0,1,2};
		String values[] = {"Off", "SnowyBiomes", "Everywhere"};
		
		SnowMode = new ModMappedMultiOption("Snowfall");
		for(int x = 0; x < keys.length; x++) {
			SnowMode.addValue(keys[x], values[x]);
		}
		
		SnowRate = new ModSliderOption("SnowRate", 1, 15);
		SnowPerTick = new ModSliderOption("SnowPerTick", 1, 30);
		
		climate.addOption(SnowPerTick);
		climate.addOption(SnowRate);
		climate.addOption(SnowMode);
    	
    	System.out.printf("SnowMod v0.5 by CJ - Loaded.\n");
		//System.out.printf("Snow Mode = %d\n", SnowMode);
		//System.out.printf("Snow Proba = %d\n", SnowRate);
		//System.out.printf("Snow Per Tick = %d\n", SnowPerTick);
		//System.out.printf("Mayhem Mode = %b\n", Mayhem);
		//System.out.printf("Snow Layers = %d\n\n", SnowLayers);
    }

    public String Version()
    {
        return "SnowMod v0.5 for MC 1.4_01";
    }
}