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

public class BlockReed extends BlockMortal
{
	//=====================
	// BEGIN NATURE OVERHAUL
	//=====================
	protected float optRain = 0.8F;
	protected float optTemp = 0.8F;
	//=====================
	// END NATURE OVERHAUL
	//=====================
	
    protected BlockReed(int i, int j)
    {
        super(i, Material.plants);
        blockIndexInTexture = j;
        float f = 0.375F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
        setTickOnLoad(true);
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if(world.isAirBlock(i, j + 1, k))
        {
            int l;
            for(l = 1; world.getBlockId(i, j - l, k) == blockID; l++) { }
            if(l < 3)
            {
                int i1 = world.getBlockMetadata(i, j, k);
                if(i1 == 15)
                {
                    world.setBlockWithNotify(i, j + 1, k, blockID);
                    world.setBlockMetadataWithNotify(i, j, k, 0);
                } else
                {
                    world.setBlockMetadataWithNotify(i, j, k, i1 + 1);
                }
            }
        }
		
		
		//========
		// BEGIN NATURE OVERHAUL
		//========
        if(!world.multiplayerWorld) {
        	ModOptions reed = mod_AutoForest.reed;
			boolean grow = ((ModBooleanOption) reed.getOption("ReedsGrow")).getValue();
			if(grow) {
				attemptGrowth(world, i, j, k, getGrowthProb(world, i, j, k));
			}
			
			// ATTEMPT DEATH
			boolean death = mod_AutoForest.reedDeath.getValue();
			if(death && hasDied(world, i, j, k)) {
				death(world, i, j, k);
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
		
		float freq = ((ModMappedOption) mod_AutoForest.reed.getOption("ReedGrowthRate")).getValue();
		
		if(mod_AutoForest.biomeModifiedGrowth.getValue()) {
			if((biome.rainfall == 0) || (biome.temperature > 1F)) {
				return 0F;
			} else {
				freq = (int) freq * getOptValueMult(biome.rainfall, optRain, 1F);
				freq = (int) freq * getOptValueMult(biome.temperature, optTemp, 1F);
		
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
		
		float freq = ((ModMappedOption) mod_AutoForest.reed.getOption("ReedDeathRate")).getValue();
		
		if(mod_AutoForest.biomeModifiedGrowth.getValue()) {
			if((biome.rainfall == 0) || (biome.temperature > 1F)) {
				return 1F;
			} else {
				freq = freq * getOptValueMult(biome.rainfall, optRain, 5F);
				freq = freq * getOptValueMult(biome.temperature, optTemp, 5F);
		
				return 1F / (3F * freq);
			}
		} else {
			return 1F / (3F * freq);
		}
	}
	
	/**
	* Death action; remove all reeds above and below
	*
	* @param	world
	* @param	i
	* @param	j
	* @param	k
	*/
	public void death(World world, int i, int j, int k) {
		int y = j;
		// Put y to the top so to avoid any reeds being dropped
		// since this is deat
		while(world.getBlockId(i, y + 1, k) == blockID) {
			y = y + 1;
		}
		// Now scan back down and delete
		while(world.getBlockId(i, y, k) == blockID) {
			world.setBlockWithNotify(i, y, k, 0);
			y--;
		}
	}

	//========
	// END NATURE OVERHAUL
	//========
    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        int l = world.getBlockId(i, j - 1, k);
        if(l == blockID)
        {
            return true;
        }
        if(l != Block.grass.blockID && l != Block.dirt.blockID && l != Block.sand.blockID)
        {
            return false;
        }
        if(world.getBlockMaterial(i - 1, j - 1, k) == Material.water)
        {
            return true;
        }
        if(world.getBlockMaterial(i + 1, j - 1, k) == Material.water)
        {
            return true;
        }
        if(world.getBlockMaterial(i, j - 1, k - 1) == Material.water)
        {
            return true;
        }
        return world.getBlockMaterial(i, j - 1, k + 1) == Material.water;
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
        checkBlockCoordValid(world, i, j, k);
    }

    protected final void checkBlockCoordValid(World world, int i, int j, int k)
    {
        if(!canBlockStay(world, i, j, k))
        {
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlockWithNotify(i, j, k, 0);
        }
    }

    public boolean canBlockStay(World world, int i, int j, int k)
    {
        return canPlaceBlockAt(world, i, j, k);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        return null;
    }

    public int idDropped(int i, Random random, int j)
    {
        return Item.reed.shiftedIndex;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public int getRenderType()
    {
        return 1;
    }
}
