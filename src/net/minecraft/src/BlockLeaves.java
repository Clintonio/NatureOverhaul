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

public class BlockLeaves extends BlockLeavesBase implements Growable
{

    private int baseIndexInPNG;
    int adjacentTreeBlocks[];

    protected BlockLeaves(int i, int j)
    {
        super(i, j, Material.leaves, false);
        baseIndexInPNG = j;
        setTickOnLoad(true);
    }

    public int getBlockColor()
    {
        double d = 0.5D;
        double d1 = 1.0D;
        return ColorizerFoliage.getFoliageColor(d, d1);
    }

    public int getRenderColor(int i)
    {
        if((i & 1) == 1)
        {
            return ColorizerFoliage.getFoliageColorPine();
        }
        if((i & 2) == 2)
        {
            return ColorizerFoliage.getFoliageColorBirch();
        } else
        {
            return ColorizerFoliage.getFoliageColorBasic();
        }
    }

    public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k)
    {
        int l = iblockaccess.getBlockMetadata(i, j, k);
        if((l & 1) == 1)
        {
            return ColorizerFoliage.getFoliageColorPine();
        }
        if((l & 2) == 2)
        {
            return ColorizerFoliage.getFoliageColorBirch();
        } else
        {
            return iblockaccess.getWorldChunkManager().getBiomeGenAt(i, k).getFoliageColorAtCoords(iblockaccess, i, j, k);
        }
    }

    public void onBlockRemoval(World world, int i, int j, int k)
    {
        int l = 1;
        int i1 = l + 1;
        if(world.checkChunksExist(i - i1, j - i1, k - i1, i + i1, j + i1, k + i1))
        {
            for(int j1 = -l; j1 <= l; j1++)
            {
                for(int k1 = -l; k1 <= l; k1++)
                {
                    for(int l1 = -l; l1 <= l; l1++)
                    {
                        int i2 = world.getBlockId(i + j1, j + k1, k + l1);
                        if(i2 == Block.leaves.blockID)
                        {
                            int j2 = world.getBlockMetadata(i + j1, j + k1, k + l1);
                            world.setBlockMetadata(i + j1, j + k1, k + l1, j2 | 8);
                        }
                    }

                }

            }

        }
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if(world.multiplayerWorld)
        {
            return;
        }
        int l = world.getBlockMetadata(i, j, k);
        if((l & 8) != 0 && (l & 4) == 0)
        {
            byte byte0 = 4;
            int i1 = byte0 + 1;
            byte byte1 = 32;
            int j1 = byte1 * byte1;
            int k1 = byte1 / 2;
            if(adjacentTreeBlocks == null)
            {
                adjacentTreeBlocks = new int[byte1 * byte1 * byte1];
            }
            if(world.checkChunksExist(i - i1, j - i1, k - i1, i + i1, j + i1, k + i1))
            {
                for(int l1 = -byte0; l1 <= byte0; l1++)
                {
                    for(int k2 = -byte0; k2 <= byte0; k2++)
                    {
                        for(int i3 = -byte0; i3 <= byte0; i3++)
                        {
                            int k3 = world.getBlockId(i + l1, j + k2, k + i3);
                            if(k3 == Block.wood.blockID)
                            {
                                adjacentTreeBlocks[(l1 + k1) * j1 + (k2 + k1) * byte1 + (i3 + k1)] = 0;
                                continue;
                            }
                            if(k3 == Block.leaves.blockID)
                            {
                                adjacentTreeBlocks[(l1 + k1) * j1 + (k2 + k1) * byte1 + (i3 + k1)] = -2;
                            } else
                            {
                                adjacentTreeBlocks[(l1 + k1) * j1 + (k2 + k1) * byte1 + (i3 + k1)] = -1;
                            }
                        }

                    }

                }

                for(int i2 = 1; i2 <= 4; i2++)
                {
                    for(int l2 = -byte0; l2 <= byte0; l2++)
                    {
                        for(int j3 = -byte0; j3 <= byte0; j3++)
                        {
                            for(int l3 = -byte0; l3 <= byte0; l3++)
                            {
                                if(adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + (l3 + k1)] != i2 - 1)
                                {
                                    continue;
                                }
                                if(adjacentTreeBlocks[((l2 + k1) - 1) * j1 + (j3 + k1) * byte1 + (l3 + k1)] == -2)
                                {
                                    adjacentTreeBlocks[((l2 + k1) - 1) * j1 + (j3 + k1) * byte1 + (l3 + k1)] = i2;
                                }
                                if(adjacentTreeBlocks[(l2 + k1 + 1) * j1 + (j3 + k1) * byte1 + (l3 + k1)] == -2)
                                {
                                    adjacentTreeBlocks[(l2 + k1 + 1) * j1 + (j3 + k1) * byte1 + (l3 + k1)] = i2;
                                }
                                if(adjacentTreeBlocks[(l2 + k1) * j1 + ((j3 + k1) - 1) * byte1 + (l3 + k1)] == -2)
                                {
                                    adjacentTreeBlocks[(l2 + k1) * j1 + ((j3 + k1) - 1) * byte1 + (l3 + k1)] = i2;
                                }
                                if(adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1 + 1) * byte1 + (l3 + k1)] == -2)
                                {
                                    adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1 + 1) * byte1 + (l3 + k1)] = i2;
                                }
                                if(adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + ((l3 + k1) - 1)] == -2)
                                {
                                    adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + ((l3 + k1) - 1)] = i2;
                                }
                                if(adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + (l3 + k1 + 1)] == -2)
                                {
                                    adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + (l3 + k1 + 1)] = i2;
                                }
                            }

                        }

                    }

                }

            }
            int j2 = adjacentTreeBlocks[k1 * j1 + k1 * byte1 + k1];
            if(j2 >= 0)
            {
                world.setBlockMetadata(i, j, k, l & -9);
            } else
            {
                removeLeaves(world, i, j, k);
            }
			
        }		
		
		//========
		// BEGIN NATURE OVERHAUL
		//========
		if(!world.multiplayerWorld) {
			attemptGrowth(world, i, j, k);
		}
		
	}
	
	private boolean attemptGrowth(World world, int i, int j, int k) {
		// Mod options for autoforest
		ModOptions mo = mod_AutoForest.options;
		
		// The values for BOTH and GROWTH are odd
		boolean growSaps 	= (((int) mod_AutoForest.growthType.getValue() % 2) == 1);
		boolean appleGrowth = ((ModBooleanOption) mo.getSubOption(mod_AutoForest.TREE_MENU_NAME)
								.getOption("ApplesGrow")).getValue();
		boolean cocoaGrowth = ((ModBooleanOption) mo.getSubOption(mod_AutoForest.TREE_MENU_NAME)
								.getOption("CocoaGrows")).getValue();
								
		// Sapling growth frequency
		float sapFreq 		= getSaplingGrowthProb(world, i, j, k);
		// Apple growth frequency
		float appleFreq 	= getAppleGrowthProb(world, i, j, k);
		// Cocoa frequency
		float cocoaFreq 	= getCocoaGrowthProb(world, i, j, k);
		
		if(growSaps && growth(sapFreq)) {
			// Try to emit a sapling
			if(world.getBlockId(i, j + 1, k) == 0) {
				emitItem(world, i, j + 1, k, new ItemStack(Block.sapling, 1,
										world.getBlockMetadata(i, j, k) % 4));
				return true;
			} 
		} else if(appleGrowth && growth(appleFreq)) {
			if((world.getBlockId(i, j - 1, k) == 0) && (appleCanGrow(world,i,j,k))) {
				emitItem(world, i, j - 1, k, new ItemStack(Item.appleRed));
				return true;
			}
		} else if((cocoaGrowth) && (growth(cocoaFreq))) {
			if((world.getBlockId(i, j - 1, k) == 0) && (cocoaCanGrow(world,i,j,k))) {
				//System.out.println("COCOA GROWTH IN RAINFOREST ("+i+","+j+","+k+")");
				emitItem(world, i, j - 1, k, new ItemStack(Item.dyePowder, 1, 3));
				return true;
			}
		}
		return false;
    }
	
	/**
	* Emit a specific item
	*
	* @param item	Item to emit
	*/
	private void emitItem(World world, int i, int j, int k, ItemStack item) {
		EntityItem entityitem = new EntityItem(world, i, j, k, item, true);
		world.spawnEntityInWorld(entityitem);
	}
	
	/** 
	* Attempts to emit an item at this location
	* Will emit either sapling, apple or cocoa
	*/
	public void grow(World world, int i, int j, int k) {
		Random rand 	= new Random();	
		int randInt 	= rand.nextInt(100);	
		
		if((cocoaCanGrow(world, i, j, k)) && (randInt < 10)) {
			emitItem(world, i, j - 1, k, new ItemStack(Item.dyePowder, 1, 3));
		} else if((appleCanGrow(world, i, j, k)) && (randInt < 25)) {
			emitItem(world, i, j - 1, k, new ItemStack(Item.appleRed));
		} else {
			emitItem(world, i, j + 1, k, new ItemStack(Block.sapling, 1,
										world.getBlockMetadata(i, j, k) % 4));
		}
	}
	
	/**
	* Check if an cocoa can grow in this biome
	*
	* @return	true if can grow here
	*/
	private boolean cocoaCanGrow(World world, int i, int j, int k) {
		BiomeGenBase biome = BiomeUtil.getBiome(i,k);
		
		// Apples can grow in the named biomes
		return ((biome.temperature >= 0.7F) && (biome.temperature <= 1.5F) 
				&& (biome.rainfall >= 0.8F));
	}
	
	/**
	* Check if an apple can grow here
	* 
	* @param	world	MC World
	* @param	i		X Coord
	* @param	j		Y Coord
	* @param	k		Z Coord
	* @return	True if it can grow in these coordinates
	*/
	private boolean appleCanGrow(World world, int i, int j, int k) {
		// Get biome info
		BiomeGenBase biome = BiomeUtil.getBiome(i, k);
		
		// Apples can grow in the named biomes
		return ((biome.temperature >= 0.7F) && (biome.temperature <= 1.0F) 
				&& (biome.rainfall > 0.4F));
	}
	/**
	* Check if there has been growth in these leaves
	*
	* @param	prob		Probability to spawn
	* @return	True if there is an item grown
	*/
	protected boolean growth(float prob) {
		// average time, in mins, between sapling pawning.
		// Remember that each tree has between 13 and 30 leaves facing "up". 
		// make number ~15 times larger if you want to do
		// "average saplings per tree" rather than "leaf block"
		// Since tickRate() is 10, we only use 6 as the mult.
		double tmp = Math.random();
		//System.out.println("Rand: " + tmp + " against " + freq);
		/*if(tmp < 0.001D) {
			System.out.println(tmp);
		}*/
		return (tmp < prob);
	}
	
	private float getSaplingGrowthProb(World world, int i, int j, int k) {
		float freq = 0.001F;
		ModMultiOption o = (ModMultiOption) ModOptionsAPI
							.getModOptions(mod_AutoForest.MENU_NAME)
							.getSubOption(mod_AutoForest.SAPLING_MENU_NAME)
							.getOption("GrowthRate");
		String v = o.getValue();
		// 20 leaves per tree
		// 3 ticks per minute
		// 180 ticks per hour
		// 4320 ticks per day
		// Super slow wants one tree to reproduce every human day
		if(v.equals("SUPERSLOW")) {
			freq = 0.0000231481481F; 
		// Slow is a new tree per tree every 6 hours 
		} else if(v.equals("SLOW")) {
			freq = 0.0000925925926F; 
		// A tree every 3 hours
		} else if(v.equals("AVERAGE")) {
			freq = 0.000185185185F; 
		//
		} else if(v.equals("FAST")) {
			freq = 0.000555555556F;
		} else if(v.equals("SUPERFAST")) {
			freq = 0.0037037037F; // Each leaf @15 mins. Each tree ~1 min: EXTREMELY FAST
		} else if(v.equals("INSANE")) {
			freq = 9.5F;
		}
		
		BiomeGenBase biome = BiomeUtil.getBiome(i, k);
		
		if(mod_AutoForest.biomeModifiedGrowth.getValue()) {
			freq = (int) freq * getOptValueMult(biome.rainfall, 0.8F, 10F);
			freq = (int) freq * getOptValueMult(biome.temperature, 0.8F, 0.1F);
		}
		
		return freq;
	}
	
	/**
	* Returns apple freq
	*
	* @return	apple freq
	*/
	private float getAppleGrowthProb(World world, int i, int j, int k) {
		float freq = ((ModMappedOption) ModOptionsAPI
			.getModOptions(mod_AutoForest.MENU_NAME)
			.getSubOption(mod_AutoForest.TREE_MENU_NAME)
			.getOption("AppleGrowthRate")).getValue() * 1.5F;
		
		BiomeGenBase biome = BiomeUtil.getBiome(i, k);
		
		if(mod_AutoForest.biomeModifiedGrowth.getValue()) {
			if((biome.rainfall == 0) || (biome.temperature > 1.5F)) {
				return 0F;
			} else {
				freq = (int) freq * getOptValueMult(biome.rainfall, 0.8F, 4F);
				freq = (int) freq * getOptValueMult(biome.temperature, 0.7F, 4F);
		
				return 1F / freq;
			}
		} else {
			return 1F / freq;
		}
	}

	/**
	* Returns cocoa growth rate
	*
	* @return	float probability
	*/
	private float getCocoaGrowthProb(World world, int i, int j, int k) {
		float freq = ((ModMappedOption) ModOptionsAPI
			.getModOptions(mod_AutoForest.MENU_NAME)
			.getSubOption(mod_AutoForest.TREE_MENU_NAME)
			.getOption("CocoaGrowthRate")).getValue();
		
		BiomeGenBase biome = BiomeUtil.getBiome(i, k);
		
		if(mod_AutoForest.biomeModifiedGrowth.getValue()) {
			if((biome.rainfall == 0) || (biome.temperature > 1.5F)) {
				return 0F;
			} else {
				freq = (int) freq * getOptValueMult(biome.rainfall, 1.0F, 15F);
				freq = (int) freq * getOptValueMult(biome.temperature, 1.0F, 15F);
		
				return 1F / freq;
			}
		} else {
			return 1F / freq;
		}
	}
	
	/**
	* Removes leaves from a tree when it's dying
	* Now integrated with NatureOverhaul
	* Removes leaves when forest growth is on at the rate
	* known to autoforest
	*/
    private void removeLeaves(World world, int i, int j, int k) {
		if(!world.multiplayerWorld) {
			boolean appleGrowth = ((ModBooleanOption) ModOptionsAPI
									.getModOptions(mod_AutoForest.MENU_NAME)
									.getSubOption(mod_AutoForest.TREE_MENU_NAME)
									.getOption("ApplesGrow")).getValue();
			// The values for BOTH and DECAY are the higher ones
			boolean growSaps 	= ((int) mod_AutoForest.growthType.getValue() > 1);
			if(growSaps) {
				// Use increased growth rate here
				if(growth(getSaplingGrowthProb(world, i, j, k) * 50)) {
					emitItem(world, i, j, k, new ItemStack(Block.sapling, 1, 
											 world.getBlockMetadata(i, j, k) % 4));
				}
			}
			
			// Check if apples grow
			if(appleGrowth) {
				if(growth(getAppleGrowthProb(world, i, j, k))) {
					emitItem(world, i, j, k, new ItemStack(Item.appleRed));
				}
			}
		} else {
			dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
		}
        world.setBlockWithNotify(i, j, k, 0);
    }
	
	/**
	* Calculate an optimal distance coefficient. This is for cases
	* where a larger number is desired the furhter you get from
	* the optimal value. The minimum is 1, so multiplying any number "r"
	* by the result of this operation will result in "r" if r is equal to "opt".
	* If r is extremely far from opt, the coefficient will be extremely large
	*
	* @param	rain		Current value
	* @param	opt		Optimal value
	* @param	tol		tolerance (lower = Higher probability)
	* @return The modifier. Output always >= 1, where 1 is "just as likely" and
	* 		higher is "less likely"
	*/
	protected float getOptValueMult(float rain, float opt, float tol) {
		return tol * (float) Math.pow(opt - rain, 2) + 1;
	}
	
	//========
	// END NATURE OVERHAUL
	//========

    public int quantityDropped(Random random)
    {
        return random.nextInt(20) != 0 ? 0 : 1;
    }

    public int idDropped(int i, Random random, int j)
    {
        return Block.sapling.blockID;
    }

    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l)
    {
        if(!world.multiplayerWorld && entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().itemID == Item.shears.shiftedIndex)
        {
            entityplayer.addStat(StatList.mineBlockStatArray[blockID], 1);
            dropBlockAsItem_do(world, i, j, k, new ItemStack(Block.leaves.blockID, 1, l & 3));
        } else
        {
            super.harvestBlock(world, entityplayer, i, j, k, l);
        }
    }

    protected int damageDropped(int i)
    {
        return i & 3;
    }

    public boolean isOpaqueCube()
    {
        return !graphicsLevel;
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if((j & 3) == 1)
        {
            return blockIndexInTexture + 80;
        } else
        {
            return blockIndexInTexture;
        }
    }

    public void setGraphicsLevel(boolean flag)
    {
        graphicsLevel = flag;
        blockIndexInTexture = baseIndexInPNG + (flag ? 0 : 1);
    }

    public void onEntityWalking(World world, int i, int j, int k, Entity entity)
    {
        super.onEntityWalking(world, i, j, k, entity);
    }
}
