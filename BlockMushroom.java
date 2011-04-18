package net.minecraft.src;

import java.util.Random;

import net.minecraft.src.modoptionsapi.ModBooleanOption;
import net.minecraft.src.modoptionsapi.ModMappedMultiOption;
import net.minecraft.src.modoptionsapi.ModOptions;
import net.minecraft.src.modoptionsapi.ModOptionsAPI;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 


public class BlockMushroom extends BlockFlower
{

    protected BlockMushroom(int i, int j)
    {
        super(i, j);
        float f = 0.2F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
    }

    protected boolean canThisPlantGrowOnThisBlockID(int i)
    {
        return Block.opaqueCubeLookup[i];
    }

    public boolean canBlockStay(World world, int i, int j, int k)
    {
        return world.getBlockLightValue(i, j, k) <= 13 && canThisPlantGrowOnThisBlockID(world.getBlockId(i, j - 1, k));
    }
    
	//========
	// BEGIN AUTOFOREST
	//========
    protected String growthModifierType = "ShroomSpawn";
	protected String deathModifierName  = "ShroomDeath";
	
	public void updateTick(World world, int i, int j, int k, Random random)
	{
		if(!world.multiplayerWorld)
		{
			ModOptions shrooms = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME)
			.getSubOption(mod_AutoForest.PLANT_MENU_NAME)
				.getSubOption(mod_AutoForest.SHROOMS_MENU_NAME);
			boolean grow = ((ModBooleanOption) shrooms.getOption("ShroomsGrow")).getValue();
			if(grow)
			{
				double growthRate = 1D /(((ModMappedMultiOption) shrooms
						.getOption("ShroomGrowthRate")).getValue());
				attemptGrowth(world, i, j, k, growthRate);
			}
			
			// ATTEMPT DEATH
			boolean death = mod_AutoForest.shroomDeath.getValue();
			double deathProb = 1D / (1.5D * (((ModMappedMultiOption) shrooms
						.getOption("ShroomDeathRate")).getValue()));
			if(death && hasDied(world, i, j, k, deathProb)) {
				System.out.println("SHROOM DIED");
				death(world, i, j, k);
			} else {
				System.out.println(deathProb);
			}
		}
        func_268_h(world, i, j, k);
    }
	//========
	// END AUTOFOREST
	//========
	
}
