package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;
import net.minecraft.src.modoptionsapi.ModOptionsAPI;
import net.minecraft.src.modoptionsapi.ModOptions;
import net.minecraft.src.modoptionsapi.ModMappedMultiOption;
import net.minecraft.src.modoptionsapi.ModBooleanOption;

//====================
// BEGIN AUTOFOREST
//====================
public class BlockSapling extends BlockFlower
{

    protected BlockSapling(int i, int j)
    {
        super(i, j);
        float f = 0.4F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
    }

    public void updateTick(World world, int i, int j, int k, Random random) {
		ModOptions options = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME).getSubOption(mod_AutoForest.TREE_MENU_NAME);
		ModMappedMultiOption mo = (ModMappedMultiOption) options.getOption("TreeGrowthRate");
		
		int bound = mo.getValue();
		
        super.updateTick(world, i, j, k, random);
		// bound is *3 because metadata is now 3 times smaller
		// due to type addition
        if((world.getBlockLightValue(i, j + 1, k) >= 9) && 
			((bound == 0) || (random.nextInt(bound * 3) == 0))) { 
            int l = world.getBlockMetadata(i, j, k);
			// Added bound > 0 to ensure INSTANT is instant
			// Add 4 each time to avoid breaking the sapling
			// specific growth
            if((l < 13) && (bound > 0)) {
                world.setBlockMetadataWithNotify(i, j, k, l | 8);
            } else {
                growTree(world, i, j, k, random, false);
            }
        }
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        j &= 3;
        if(j == 1)
        {
            return 63;
        }
        if(j == 2)
        {
            return 79;
        } else
        {
            return super.getBlockTextureFromSideAndMetadata(i, j);
        }
    }
	
	/**
	* Grow a tree ignoring death
	*/
	public void growTree(World world, int i, int j, int k, Random random) {
		growTree(world, i, j, k, random, true);
	}
	
	/**
	* Attempt to Grow a tree
	*
	* @param 	ignoreDeath		True if we should force grow the sapling
	*/
    private void growTree(World world, int i, int j, int k, Random random, boolean ignoreDeath) {
		// Check options
		ModOptions mo = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME);
		ModOptions saps = mo.getSubOption(mod_AutoForest.SAPLING_MENU_NAME);
		ModBooleanOption sapDeathOp = (ModBooleanOption) saps.getOption("SaplingDeath");
		
		// Rate of big tree growth:
		int bigTreeRate = mod_AutoForest.getBiomeModifier(world.getBiomeName(i,k), 
							"BigTree");
		
		// Choose a generator
		Object obj = null;
		
		//%3 as the meta data is on a %3 basis, where the 0th, 1st and 2nd index 
		// are for type, the rest is for timing tree growth
		int type = world.getBlockMetadata(i,j,k) % 3;
		if(type == 1) {
			if(random.nextInt(3) == 0) {
				obj = new WorldGenTaiga1();
			} else {
				obj = new WorldGenTaiga2();
			}
		} else if(type == 2) {
			obj = new WorldGenForest();
		} else if(random.nextInt(100) < bigTreeRate) {
			obj = new WorldGenBigTree();
		} else {
			obj = new WorldGenTrees();
		}
		
        world.setBlockWithNotify(i, j, k, 0); 
		
		// Ignore death of saplings
		if(ignoreDeath) {
			if(!((WorldGenerator) (obj)).generate(world, random, i, j, k)) {
				world.setBlock(i, j, k, blockID);
			}
		// Sapling has a random chance of dying instead of growing
        } else if((!starvedSapling(world, i, j, k)) && (!randomDeath(world, i, j, k, random)) 
			&& (!((WorldGenerator) (obj)).generate(world, random, i, j, k))
			&& (!sapDeathOp.getValue())) {
			world.setBlock(i, j, k, blockID);
        }
    }

    protected int damageDropped(int i)
    {
        return i & 3;
    }

	
	/**
	* Check if a sapling is starved by it's neighbours
	*
	* @return	true is starved
	*/
	private boolean starvedSapling(World world, int i, int j, int k) {
		// Check options
		ModOptions mo = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME);
		ModOptions saps = mo.getSubOption(mod_AutoForest.SAPLING_MENU_NAME);
		boolean sapDeathOp = ((ModBooleanOption) saps.getOption("SaplingDeath")).getValue();
		
		if(sapDeathOp) {
			// Min dist between trees
			int dist = mod_AutoForest.getBiomeModifier(world.getBiomeName(i,k),
							"TreeGap");
			
			for(int x = i - dist; x <= dist + i; x++) {
				for(int z = k - dist; z <= dist + k; z++) {
					if(world.getBlockId(x, j, z) == Block.wood.blockID) {
						System.out.println("STARVED SAPLING IN BIOME: " + world.getBiomeName(i,k) + ". DIST: " + dist);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	* Check if a sapling has died due to natural causes
	*
	* @return	True if a sapling has died
	*/
	private boolean randomDeath(World world, int i, int j, int k, Random random) {
		// Check options
		ModOptions mo = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME);
		ModOptions saps = mo.getSubOption(mod_AutoForest.SAPLING_MENU_NAME);
		boolean sapDeathOp = ((ModBooleanOption) saps.getOption("SaplingDeath")).getValue();
		String biomeName = world.getBiomeName(i,k);
		int deathRate = mod_AutoForest.getBiomeModifier(biomeName, "SaplingDeath");
		
		// This means that a sapling with a death rate of X will die x% of the time
		if((sapDeathOp) && (random.nextInt(100) >= 100 - deathRate)) {
			System.out.println("SAPLING RANDOM DEATH IN BIOME: "+ biomeName + " AT RATE " + deathRate);
			return true;
		}
		
		return false;
	}
}	
//====================
// END AUTOFOREST
//====================
