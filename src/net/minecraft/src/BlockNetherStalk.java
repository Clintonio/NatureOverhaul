// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.Random;
//====================
// BEGIN NATURE OVERHAUL
//====================
import moapi.*;
//====================
// END NATURE OVERHAUL
//====================

// Referenced classes of package net.minecraft.src:
//            BlockFlower, Block, World, WorldChunkManager, 
//            BiomeGenHell, ItemStack, Item

public class BlockNetherStalk extends BlockFlower
{

    protected BlockNetherStalk(int i)
    {
        super(i, 226);
        setTickOnLoad(true);
        float f = 0.5F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }

    protected boolean canThisPlantGrowOnThisBlockID(int i)
    {
        return i == Block.slowSand.blockID;
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        int l = world.getBlockMetadata(i, j, k);
        if(l < 3)
        {
            WorldChunkManager worldchunkmanager = world.getWorldChunkManager();
            if(worldchunkmanager != null)
            {
                BiomeGenBase biomegenbase = worldchunkmanager.getBiomeGenAt(i, k);
                if((biomegenbase instanceof BiomeGenHell) && random.nextInt(15) == 0)
                {
                    l++;
                    world.setBlockMetadataWithNotify(i, j, k, l);
                }
            }
        }
        		
		//========
		// BEGIN NATURE OVERHAUL
		//========
		// ATTEMPT REPRODUCTION
		if(!world.multiplayerWorld) {
			boolean grow = mod_AutoForest.wortsGrow.getValue();
			if(grow) {
				attemptGrowth(world, i, j, k);
			}
		
			// ATTEMPT DEATH
			boolean death = mod_AutoForest.wortDeath.getValue();
			if(death && hasDied(world, i, j, k)) {
				death(world, i, j, k);
			}
		}
		//========
		// END NATURE OVERHAUL
		//========
        super.updateTick(world, i, j, k, random);
    }
	//========
	// BEGIN NATURE OVERHAUL
	//========
	
	/**
	* Get the growth probability
	*
	* @return	Growth probability
	*/
	public float getGrowthProb(World world, int i, int j, int k) {
		float freq = ((ModMappedOption) mod_AutoForest.wort.getOption("WortGrowthRate")).getValue();
		
		return 1F / freq;
	}
	
	/**
	* Get the death probability
	*
	* @return	Death probability
	*/
	public float getDeathProb(World world, int i, int j, int k) {
		float freq = ((ModMappedOption) mod_AutoForest.wort.getOption("WortDeathRate")).getValue();
		return 1F / freq;
	}
	//========
	// END NATURE OVERHAUL
	//========

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if(j >= 3)
        {
            return blockIndexInTexture + 2;
        }
        if(j > 0)
        {
            return blockIndexInTexture + 1;
        } else
        {
            return blockIndexInTexture;
        }
    }

    public int getRenderType()
    {
        return 6;
    }

    public void dropBlockAsItemWithChance(World world, int i, int j, int k, int l, float f, int i1)
    {
        if(world.multiplayerWorld)
        {
            return;
        }
        int j1 = 1;
        if(l >= 3)
        {
            j1 = 2 + world.rand.nextInt(3);
            if(i1 > 0)
            {
                j1 += world.rand.nextInt(i1 + 1);
            }
        }
        for(int k1 = 0; k1 < j1; k1++)
        {
            dropBlockAsItem_do(world, i, j, k, new ItemStack(Item.netherStalkSeeds));
        }

    }

    public int idDropped(int i, Random random, int j)
    {
        return Item.netherStalkSeeds.shiftedIndex;
    }

    public int quantityDropped(Random random)
    {
        return 0;
    }
}
