// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.Random;

//========
// BEGIN NATURE OVERHAUL
//========
import moapi.*;
//========
// END NATURE OVERHAUL
//========


// Referenced classes of package net.minecraft.src:
//            Block, BlockFlower, Material

public class BlockMushroomCap extends BlockGrowable
{
	//=====================
	// BEGIN NATURE OVERHAUL
	//=====================
	protected float optRain = 1.0F;
	protected float optTemp = 0.9F;
	//=====================
	// END NATURE OVERHAUL
	//=====================

    private int mushroomType;

    public BlockMushroomCap(int i, Material material, int j, int k)
    {
        super(i, j, material);
        mushroomType = k;
        setTickOnLoad(true);
    }
	
	//======================
	// BEGIN NATURE OVERHAUL
	//======================
	
    public void updateTick(World world, int i, int j, int k, Random random) {
		if(!world.multiplayerWorld) {
			ModOptions shrooms = mod_AutoForest.shrooms;
			boolean grow = ((ModBooleanOption) shrooms.getOption("ShroomsGrow")).getValue();
			if(grow && (world.getBlockId(i, j + 1, k) == 0)) {
				attemptGrowth(world, i, j, k);
			}
		}
	}
	
	/**
	* Get the growth probability
	*
	* @return	Growth probability
	*/
	public float getGrowthProb(World world, int i, int j, int k) {
		BiomeGenBase biome = BiomeUtil.getBiome(i, k);
		
		float freq = ((ModMappedOption) mod_AutoForest.shrooms.getOption("ShroomGrowthRate")).getValue();
		
		if(mod_AutoForest.biomeModifiedGrowth.getValue()) {
			if((biome.rainfall == 0) || (biome.temperature > 1F)) {
				return 0F;
			} else {
				freq = freq * getOptValueMult(biome.rainfall, optRain, 3F);
				freq = freq * getOptValueMult(biome.temperature, optTemp, 3F);
		
				return 1F / freq;
			}
		} else {
			return 1F / freq;
		}
	}

	//======================
	// END NATURE OVERHAUL
	//======================
	
    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if(j == 10 && i > 1)
        {
            return blockIndexInTexture - 1;
        }
        if(j >= 1 && j <= 9 && i == 1)
        {
            return blockIndexInTexture - 16 - mushroomType;
        }
        if(j >= 1 && j <= 3 && i == 2)
        {
            return blockIndexInTexture - 16 - mushroomType;
        }
        if(j >= 7 && j <= 9 && i == 3)
        {
            return blockIndexInTexture - 16 - mushroomType;
        }
        if((j == 1 || j == 4 || j == 7) && i == 4)
        {
            return blockIndexInTexture - 16 - mushroomType;
        }
        if((j == 3 || j == 6 || j == 9) && i == 5)
        {
            return blockIndexInTexture - 16 - mushroomType;
        }
        if(j == 14)
        {
            return blockIndexInTexture - 16 - mushroomType;
        }
        if(j == 15)
        {
            return blockIndexInTexture - 1;
        } else
        {
            return blockIndexInTexture;
        }
    }

    public int quantityDropped(Random random)
    {
        int i = random.nextInt(10) - 7;
        if(i < 0)
        {
            i = 0;
        }
        return i;
    }

    public int idDropped(int i, Random random, int j)
    {
        return Block.mushroomBrown.blockID + mushroomType;
    }
}
