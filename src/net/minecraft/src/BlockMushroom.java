// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.Random;
    
//========
// BEGIN AUTOFOREST
//========
import moapi.*;
//========
// END AUTOFOREST
//========

public class BlockMushroom extends BlockFlower
{
	//=====================
	// BEGIN NATURE OVERHAUL
	//=====================
	protected float optRain = 1.0F;
	protected float optTemp = 0.9F;
	//=====================
	// END NATURE OVERHAUL
	//=====================
	
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
		// BEGIN NATURE OVERHAUL
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
			boolean grow = mod_AutoForest.shroomTreesGrow.getValue();
			if(grow) {
				attemptGrowth(world, i, j, k);
			}
			
			// ATTEMPT DEATH
			boolean death = mod_AutoForest.shroomDeath.getValue();
			if(death && hasDied(world, i, j, k)) {
				death(world, i, j, k);
			}
		}
		//========
		// END NATURE OVERHAUL
		//========
    }
	
	//=====================
	// BEGIN NATURE OVERHAUL
	//=====================
	
	/**
	* Grow an item
	*/
	public void grow(World world, int i, int j, int k) {
		fertilizeMushroom(world, i, j, k, world.rand);
	}
	
	/**
	* Get the growth probability
	*
	* @return	Growth probability
	*/
	public float getGrowthProb(World world, int i, int j, int k) {
		BiomeGenBase biome = BiomeUtil.getBiome(i, k);
		
		float freq = mod_AutoForest.shroomTreeGrowth.getValue();
		
		if(mod_AutoForest.biomeModifiedGrowth.getValue()) {
			if((biome.rainfall == 0) || (biome.temperature > 1F)) {
				return 0F;
			} else {
				freq = freq * getOptValueMult(biome.rainfall, optRain, 1F);
				freq = freq * getOptValueMult(biome.temperature, optTemp, 1F);
		
				return 1F / freq;
			}
		} else {
			return 1F / freq;
		}
	}
	
	/**
	* Get the death probability
	*
	* @return	Death probability
	*/
	public float getDeathProb(World world, int i, int j, int k) {
		BiomeGenBase biome = BiomeUtil.getBiome(i, k);
		
		float freq = mod_AutoForest.shroomDeathRate.getValue();
		
		if(mod_AutoForest.biomeModifiedGrowth.getValue()) {
			if((biome.rainfall == 0) || (biome.temperature > 1F)) {
				return 1F;
			} else {
				freq = freq * getOptValueMult(biome.rainfall, optRain, 5F);
				freq = freq * getOptValueMult(biome.temperature, optTemp, 5F);
		
				return 1F / freq;
			}
		} else {
			return 1F / freq;
		}
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
        if(j < 0 || j >= world.worldHeight)
        {
            return false;
        } else
        {
            int l = world.getBlockId(i, j - 1, k);
            return l == Block.mycelium.blockID || world.getFullBlockLightValue(i, j, k) < 13 && canThisPlantGrowOnThisBlockID(l);
        }
    }

    public boolean fertilizeMushroom(World world, int i, int j, int k, Random random)
    {
        int l = world.getBlockMetadata(i, j, k);
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
            world.setBlockAndMetadata(i, j, k, blockID, l);
            return false;
        } else
        {
            return true;
        }
    }
}
