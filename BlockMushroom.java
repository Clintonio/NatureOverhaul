// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.Random;
    
//========
// BEGIN AUTOFOREST
//========
import modoptionsapi.ModBooleanOption;
import modoptionsapi.ModMappedMultiOption;
import modoptionsapi.ModOptions;
import modoptionsapi.ModOptionsAPI;
//========
// END AUTOFOREST
//========

public class BlockMushroom extends BlockFlower
{
    
	//========
	// BEGIN AUTOFOREST
	//========
    protected String growthModifierType = "ShroomSpawn";
	protected String deathModifierName  = "ShroomDeath";
	//========
	// END AUTOFOREST
	//========

    protected BlockMushroom(int i, int j)
    {
        super(i, j);
        float f = 0.2F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        setTickOnLoad(true);
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
		//========
		// BEGIN AUTOFOREST
		//========
		boolean keepDefaultSpread = mod_AutoForest.defaultShroomSpread.getValue();
        if(keepDefaultSpread && random.nextInt(100) == 0)
        {
            int l = (i + random.nextInt(3)) - 1;
            int i1 = (j + random.nextInt(2)) - random.nextInt(2);
            int j1 = (k + random.nextInt(3)) - 1;
            if(world.isAirBlock(l, i1, j1) && canBlockStay(world, l, i1, j1))
            {
                i += random.nextInt(3) - 1;
                k += random.nextInt(3) - 1;
                if(world.isAirBlock(l, i1, j1) && canBlockStay(world, l, i1, j1))
                {
                    world.setBlockWithNotify(l, i1, j1, blockID);
                }
            }
        }
		
		if(!world.multiplayerWorld) {
			ModOptions shrooms = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME)
			.getSubOption(mod_AutoForest.PLANT_MENU_NAME)
				.getSubOption(mod_AutoForest.SHROOMS_MENU_NAME);
			boolean grow = ((ModBooleanOption) shrooms.getOption("ShroomsGrow")).getValue();
			if(grow) {
				double growthRate = 1D /(((ModMappedMultiOption) shrooms
						.getOption("ShroomGrowthRate")).getValue());
			}
			
			// ATTEMPT DEATH
			boolean death = mod_AutoForest.shroomDeath.getValue();
			double deathProb = 1D / (1.5D * (((ModMappedMultiOption) shrooms
						.getOption("ShroomDeathRate")).getValue()));
			if(death && hasDied(world, i, j, k, deathProb)) {
				death(world, i, j, k);
			}
		}
		//========
		// END AUTOFOREST
		//========
    }
	
	

    protected boolean canThisPlantGrowOnThisBlockID(int i)
    {
        return Block.opaqueCubeLookup[i];
    }

    public boolean canBlockStay(World world, int i, int j, int k)
    {
        if(j < 0 || j >= 128)
        {
            return false;
        } else
        {
            return world.getFullBlockLightValue(i, j, k) < 13 && canThisPlantGrowOnThisBlockID(world.getBlockId(i, j - 1, k));
        }
    }
}
