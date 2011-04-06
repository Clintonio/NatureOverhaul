package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 


public class mod_CrystalClearWaters extends BaseMod
{

    public mod_CrystalClearWaters()
    {
        if(WaterOpacity <= 0)
        {
            Block.waterStill.setLightOpacity(0);
            Block.waterMoving.setLightOpacity(0);
        } else
        {
            Block.waterStill.setLightOpacity(WaterOpacity);
            Block.waterMoving.setLightOpacity(WaterOpacity);
        }
    }

    public String Version()
    {
        return "v3.01 for Beta 1.3_01";
    }

    public static int WaterOpacity = PReader.PropInt("/mods/Roundaround/CrystalClearWaters/CrystalClearWaters.properties", "Water_Opacity", "1");

}
