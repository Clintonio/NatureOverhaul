// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.Random;
    
//========
// BEGIN AUTOFOREST
//========
import moapi.ModBooleanOption;
import moapi.ModMappedMultiOption;
import moapi.ModOptions;
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
            byte byte0 = 4;
            int l = 5;
            for(int i1 = i - byte0; i1 <= i + byte0; i1++)
            {
                for(int k1 = k - byte0; k1 <= k + byte0; k1++)
                {
                    for(int i2 = j - 1; i2 <= j + 1; i2++)
                    {
                        if(world.getBlockId(i1, i2, k1) == blockID && --l <= 0)
                        {
                            return;
                        }
                    }

                }

            }

            int j1 = (i + random.nextInt(3)) - 1;
            int l1 = (j + random.nextInt(2)) - random.nextInt(2);
            int j2 = (k + random.nextInt(3)) - 1;
            for(int k2 = 0; k2 < 4; k2++)
            {
                if(world.isAirBlock(j1, l1, j2) && canBlockStay(world, j1, l1, j2))
                {
                    i = j1;
                    j = l1;
                    k = j2;
                }
                j1 = (i + random.nextInt(3)) - 1;
                l1 = (j + random.nextInt(2)) - random.nextInt(2);
                j2 = (k + random.nextInt(3)) - 1;
            }

            if(world.isAirBlock(j1, l1, j2) && canBlockStay(world, j1, l1, j2))
            {
                world.setBlockWithNotify(j1, l1, j2, blockID);
            }
        }
		
		if(!world.multiplayerWorld) {
			ModOptions shrooms = mod_AutoForest.shrooms;
			boolean grow = mod_AutoForest.shroomTreesGrow.getValue();
			if(grow) {
				double growthRate = 1D / mod_AutoForest.shroomTreeGrowth.getValue();
				attemptGrowth(world, i, j, k, growthRate);
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
	
	//=====================
	// BEGIN NATURE OVERHAUL
	//=====================
	
	/**
	* Grow an item
	*/
	public void grow(World world, int i, int j, int k) {
		func_35293_c(world, i, j, k, world.rand);
	}
	
	//=====================
	// END NATURE OVERHAUL
	//=====================
	
	

    protected boolean canThisPlantGrowOnThisBlockID(int i)
    {
        return Block.opaqueCubeLookup[i];
    }

    public boolean canBlockStay(World world, int i, int j, int k)
    {
label0:
        {
            if(j >= 0)
            {
                world.getClass();
                if(j < 128)
                {
                    break label0;
                }
            }
            return false;
        }
        return world.getFullBlockLightValue(i, j, k) < 13 && canThisPlantGrowOnThisBlockID(world.getBlockId(i, j - 1, k));
    }

    public boolean func_35293_c(World world, int i, int j, int k, Random random)
    {
        int l = world.getBlockId(i, j - 1, k);
        if(l != Block.dirt.blockID && l != Block.grass.blockID)
        {
            return false;
        }
        int i1 = world.getBlockMetadata(i, j, k);
        world.setBlock(i, j, k, 0);
        WorldGenBigMushroom worldgenbigmushroom = null;
        if(blockID == Block.mushroomBrown.blockID)
        {
            worldgenbigmushroom = new WorldGenBigMushroom(0);
        } else
        if(blockID == Block.mushroomRed.blockID)
        {
            worldgenbigmushroom = new WorldGenBigMushroom(1);
        }
        if(worldgenbigmushroom == null || !worldgenbigmushroom.generate(world, random, i, j, k))
        {
            world.setBlockAndMetadata(i, j, k, blockID, i1);
            return false;
        } else
        {
            return true;
        }
    }
}
