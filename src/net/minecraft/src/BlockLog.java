// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.Random;
//=======================
// START NATURE OVERHAUL
//=======================
import moapi.ModOptionsAPI;
import moapi.ModOptions;
import moapi.ModBooleanOption;
import moapi.ModMappedMultiOption;
import java.util.HashSet;
//=======================
// END NATURE OVERHAUL
//=======================

public class BlockLog extends Block
{
	private static final int MAX_TREE_HEIGHT = 16;
	// Flag controls
	private static final int iBits = 5;
	private static final int jBits = 7;
	private static final int kBits = 5;
	/** 
	* The radius at which leaves are destroyed by the nature overhaul
	* algorithm providing that the option for leaf decay is enabled
	* and there is no wood within this radius
	*/
	public int leafDeathRadius = 2;
	
    protected BlockLog(int i)
    {
        super(i, Material.wood);
        setTickOnLoad(true);
        blockIndexInTexture = 20;
    }
	
	//=======================
	// START NATURE OVERHAUL
	//=======================
	public void updateTick(World world, int i, int j, int k, Random random) {
		if(!world.multiplayerWorld) {
			ModOptions mo = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME).getSubOption(mod_AutoForest.TREE_MENU_NAME);
			boolean treeDeath = ((ModBooleanOption) mo.getOption("TreeDeath")).getValue();
			// Death rate per thousand per tick
			int deathRate = ((ModMappedMultiOption) mo.getOption("DeathRate")).getValue();	 
			// Modify the rate, higher for drying biomes
			deathRate = (int) mod_AutoForest.applyBiomeModifier(deathRate, "TreeDeath",
														  world, i, k);
			if((treeDeath) &&  ((deathRate <= 0) || (random.nextInt(deathRate) == 0)) && (isTree(world, i, j, k))) {
				int lowestLogJ = getLowestLogJ(world, i, j, k);
				//System.out.println("KILLING A TREE IN biome " + mod_AutoForest.getBiomeName(i,k) + ". WITH RATE: " + deathRate);
				killTree(world, i, lowestLogJ, k);
			}
		}
	//=======================
	// END NATURE OVERHAUL
	//=======================
	}
	
    public int tickRate()
    {
        return 10;
    }

    public int quantityDropped(Random random)
    {
        return 1;
    }
	
	//=======================
	// START NATURE OVERHAUL
	//=======================
	/**
	* Check if current block is a part of tree trunk
	* Does not ignore current block
	*/
	private boolean isTree(World world, int i, int j, int k) {
		return isTree(world, i, j, k, false);
	}
	
	/**
	* Check if this current log is a part of a tree trunk
	*
	* @param	ignoreSelf		Ignores the block status at i,j,k when true
	* @return	True if is a part of a tree
	*/
	private boolean isTree(World world, int i, int j, int k, boolean ignoreSelf) {
		// How many logs we have checked
		int checked = 0;
		// We have found a dirt block below
		boolean groundFound = false;
		// Top of tree found
		boolean topFound = false;
		// It has been proven this is not a tree
		boolean isNotTree = false;
		// Surrounding Leaves found
		int leafLayersFound = 0;
		int curI = i;
		int curJ = j - 1;
		int curK = k;
		// Look down first
		while((checked <= MAX_TREE_HEIGHT) && (!groundFound) && (!isNotTree)) {
			int blockBelowID = world.getBlockId(curI, curJ, curK);
			if(blockBelowID == Block.wood.blockID) {
				curJ = curJ - 1;
			} else if((blockBelowID == Block.dirt.blockID) || (blockBelowID == Block.grass.blockID)) {
				groundFound = true;
				// Put the J back onto a known log
				curJ = curJ + 1;
			} else {
				isNotTree = true;
			}
			
			checked++;
		}
		
		// Set checked back to 0 as we are scanning the whole tree up now
		checked = 0;
		// Scan back up for leaves
		if((!isNotTree) && (groundFound)) {
			while((checked <= MAX_TREE_HEIGHT) && (!topFound) && (!isNotTree)) {
				int blockAboveID = world.getBlockId(curI, curJ, curK);
				// Continue scanning for leaves
				// After || is ignoring self block
				if((blockAboveID == Block.wood.blockID) || ((curJ == j) && (ignoreSelf))) {
					if(allLeavesAround(world, curI, curJ, curK)) {
						leafLayersFound++;
					}
					
					curJ = curJ + 1;
				// Top of tree found
				} else if(blockAboveID == Block.leaves.blockID) {
					topFound = true;
				} else {
					isNotTree = true;
				}
			}
			
			// Ground found is also true at this point by definition
			return (!isNotTree && topFound && (leafLayersFound > 0));
		} else {
			return false;
		}
	}
	
	/**
	* Kill tree, includes selfblock
	*/
	private int killTree(World world, int i, int j, int k) {
		boolean killLeaves = mod_AutoForest.leafDecay.getValue();
		
		return killTree(world, i, j, k, killLeaves);
	}
	/**
	* Kills a tree from the given block upwards
	*
	* @param	killLeaves	True if leaves should be killed
	* @return	Number of logs removed
	*/
	private int killTree(World world, int i, int j, int k, boolean killLeaves) {
		boolean ignoreSelf = (world.getBlockId(i,j,k) == 0);
		int treeHeight = getTreeHeight(world, i, j, k);
		/* Numbr of blocks removed */
		int removed = 0;
		
		// If ignore self (ie; self could be air, then skip over it)
		if(ignoreSelf) {
			j++;
		}
	
		//System.out.println("Killing tree from ("+i+","+j+","+k+") with height " + treeHeight);
		// Kill first log to avoid downscanning
		killLog(world, i, j ,k, true);
		
		HashSet<Integer> flags = new HashSet<Integer>();
		
		int[] base  = {i, j, k};
		int[] block = {i, j + 1, k};
		flagBlock(block, base, flags);
		int out = 1 + scanAndFlag(world, base, block, flags, treeHeight);
		
		if(killLeaves) {
			killLeaves(world, i, j, k, base, flags);
		}
		
		return out;
	}
	
	/**
	* Scans through the flags for leaves and removes them
	*
	* @param	flags	The encoded flags
	* @param	base		The base block (for reference)
	*/
	private void killLeaves(World world, int i, int j, int k, int[] base, HashSet<Integer> flags) {
		for(Integer flag : flags) {
			int iMod = decodeFlag(flag, iBits, jBits + kBits);
			int jMod = decodeFlag(flag, jBits, kBits);
			int kMod = decodeFlag(flag, kBits);
			
			int lI = iMod + base[0];
			int lJ = jMod + base[1];
			int lK = kMod + base[2];
			
			//System.out.println("iMod: " + iMod + ". jMod: " + jMod + ". kMod: " + kMod);
			
			//System.out.println("Kill Leaf: (" + lI + ", " + lJ + ", " + lK + "");
			if(!hasNearbyWood(world, lI, lJ, lK, leafDeathRadius)) {
				world.setBlockWithNotify(lI, lJ, lK, 0);
			}
		}
	}
	
	/**
	* Check if leaves have nearby wood
	*
	* @param	radius	Radius to check in
	* @return	True if wood nearby
	*/
	private boolean hasNearbyWood(World world, int i, int j, int k, int radius) {
		for(int x = -radius; x <= radius; x++) {
			for(int y = -radius; y <= radius; y++) {
				for(int z = -radius; z <= radius; z++) {
					if(world.getBlockId(i + x, j + y, k + z) == Block.wood.blockID) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	* Decode the modifier of a flag
	* 
	* @param	flag
	* @param	len 		length of entry
	* @param	shift	How far left the value is shifted
	* @return	Int value
	*/
	private int decodeFlag(int flag, int len, int shift) {
		return ((flag >> shift) & ((int) Math.pow(2, len) - 1)) - (int) Math.pow(2, len - 1);
	}
	
	/**
	* Decode the modifier of a flag
	* 
	* @param	flag
	* @param	len 		length of entry
	* @return	Int value
	*/
	private int decodeFlag(int flag, int len) {
		return decodeFlag(flag, len, 0);
	}
	
	
	/**
	* Scans, deletes and flags blocks for lumberjacking
	*
	* @param	block		 Current block to scan and flag
	* @param	base			 The base block
	* @param	flags		 Flag store
	* @return	Number of blocks removed
	*/
	private int scanAndFlag(World world, int[] base, int[] block, 
						HashSet<Integer> flags, int treeHeight) {
		int i = block[0];
		int j = block[1];
		int k = block[2];
		int removed = 0;
		
		//System.out.println("Scan and flag (" + i + ","+j+","+k+")");
		
		for(int[] nBlock : neighbours(block)) {
			int id = world.getBlockId(nBlock[0], nBlock[1], nBlock[2]);
			if((inRange(nBlock, base,treeHeight)) && (!isBlockFlagged(nBlock, base, flags))
				&& ((id == Block.wood.blockID) || (id == Block.leaves.blockID))) {
				flagBlock(nBlock, base, flags);
				removed = removed + scanAndFlag(world, base, nBlock, flags, treeHeight);
			}
		}
		
		// Remove the current block if it's a non-tree log
		if(world.getBlockId(i,j,k) == Block.wood.blockID) {
			if((!isTree(world, i, j, k)) || ((i == base[0]) && (k == base[2]))) {
				killLog(world, block[0], block[1], block[2], true);
				removed++;
			}
		}
		
		return removed;
	}
	
	/**
	* Get all neighbouring blocks
	*/
	private int[][] neighbours(int[] block) {
		int i = block[0];
		int j = block[1];
		int k = block[2];
		
		int[][] n = {
			{i + 1, j, k},
			{i - 1, j, k},
			{i, j + 1, k},
			{i, j - 1, k},
			{i, j, k + 1},
			{i, j, k - 1}
		};
		
		return n;
	}
	
	/**
	* Flags an individual block for the lumberjack aglorithm
	*/
	private void flagBlock(int[] block, int[] base, HashSet<Integer> flags) {
		int flag = makeFlag(block, base);
		
		flags.add(flag);
	}
	
	/**
	* check if block is flagged
	*/
	private boolean isBlockFlagged(int[] block, int[] base, HashSet<Integer> flags) {
		int flag = makeFlag(block, base);
		
		return (flags.contains(flag));
	}
	
	/**
	* Create a flag from a block, based on distance from the base
	*/
	private int makeFlag(int[] block, int base[]) {
		int i = ((block[0] - base[0]) % (int) Math.pow(2, iBits - 1)) + (int) Math.pow(2, iBits - 1);
		int j = ((block[1] - base[1]) % (int) Math.pow(2, jBits - 1)) + (int) Math.pow(2, jBits - 1);
		int k = ((block[2] - base[2]) % (int) Math.pow(2, kBits - 1)) + (int) Math.pow(2, kBits - 1);
		
		// Put i j+k bits in front so no clash with j/k
		// put j in the jth to kth bits
		// put k in the kth bits.
		// This way we can store i,j,k  as a single int
		// The size of i,j,kBits depends on the distance
		// to search in the x/z of the inRange algoirthm
		int flag = (int) (i << (jBits + kBits)) ^ (j << kBits) ^ k;

		//System.out.println("Flag pieces: " + i + ", " + j + ", " + k  +". Result: " + flag);
		
		return flag;
	}
	
	/**
	* Java lacks a mod method
	*/
	private int mod(int num, int mod) {
		return ((num = num % mod) < 0) ? num + mod : num;
	}
	
	/**
	* Check if a block is in range of the base
	*/
	private boolean inRange(int[] block, int[] base, int treeHeight) {
		// Check the x/z are within a 6 square
		if((Math.abs(block[0] - base[0]) <= 6) 
		&& (Math.abs(block[2] - base[2]) <= 6)){
			// Within the treeheight plus a little for leaves
			if((block[1] >= base[1]) &&
				(block[1] - base[1] <= treeHeight + 5)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	* Kills an individual log
	*
	* @param	treeDeath		If true then tree turns into wood
	* @param	ignoreSelf		Ignore the block at i,j,k
	*/
	private void killLog(World world, int i, int j, int k, boolean treeDeath) {
		// For 0.9.1, temporarily stop turning to wood. Consider alternate
		ItemStack blockWood = new ItemStack(this, 1, world.getBlockMetadata(i,j,k));
		world.setBlockWithNotify(i, j, k, 0);
		
		// Create a new block entity
		EntityItem ent = new EntityItem(world, i, j, k, blockWood);
		world.spawnEntityInWorld(ent);
	}
	
	/**
	* Gets height of the tree
	*/
	private int getTreeHeight(World world, int i, int j, int k) {
		int height = 1;
		int curJ = j - 1;
		// Neg first
		while(world.getBlockId(i, curJ, k) == Block.wood.blockID) {
			curJ--;
			height++;
		}
		
		curJ = j + 1;
		while(world.getBlockId(i, curJ, k) == Block.wood.blockID) {
			curJ++;
			height++;
		}
		
		return height;
	}
	
	/**
	* Gets the j location of the lowest wood block below this log
	*
	* @return	lowest wood block j location
	*/
	private int getLowestLogJ(World world, int i, int j, int k) {
		while(world.getBlockId(i, j - 1, k) == Block.wood.blockID) {
			j--;
		}
		
		return j;
	}
	
	
	/**
	* Check if a block is surrounded by leaves
	*
	* @return	True if surrounded by leaves
	*/
	private boolean allLeavesAround(World world, int i, int j, int k) {
		return ((world.getBlockId(i + 1, j, k) == Block.leaves.blockID) &&
				(world.getBlockId(i - 1, j, k) == Block.leaves.blockID) &&
				(world.getBlockId(i, j, k + 1) == Block.leaves.blockID) &&
				(world.getBlockId(i, j, k - 1) == Block.leaves.blockID));
	}
	
	/**
	* Do additional damage to an axe if we're lumberjacking a tree
	*/
	private void additionalToolDamage(EntityPlayer player, int damage) {
		ItemStack itemstack = player.getCurrentEquippedItem();
		if(itemstack != null) {
			// Damage item compared to the nmber of items found
			itemstack.damageItem(damage - 1, player);
			if(itemstack.stackSize == 0) {
				player.destroyCurrentEquippedItem();
			}
		}
	}
	
	//=======================
	// END NATURE OVERHAUL
	//=======================
	
    public int idDropped(int i, Random random, int k)
    {
        return Block.wood.blockID;
    }

    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l)
    {
        super.harvestBlock(world, entityplayer, i, j, k, l);
		//=======================
		// START NATURE OVERHAUL
		//=======================
		boolean lumberjack = ((ModBooleanOption) ModOptionsAPI
							 .getModOptions(mod_AutoForest.MENU_NAME)
							 .getSubOption(mod_AutoForest.TREE_MENU_NAME)
							 .getOption("Lumberjack")).getValue();
		// Delete entire tree on block removal
		if((!world.multiplayerWorld) && (lumberjack) && (isTree(world, i, j, k, true))) {
			// Check if player is using an axe
			ItemStack itemstack = entityplayer.getCurrentEquippedItem();
	        if(itemstack != null) {
				int id = itemstack.itemID;
				// Axe IDs only
	            if((id >= 0) && (id < Item.itemsList.length) && 
				   (Item.itemsList[id] instanceof ItemAxe)) {
	            	int damage = killTree(world, i, j, k);
	    			additionalToolDamage(entityplayer,damage);
	            }
	        }
		}
		//=======================
		// END NATURE OVERHAUL
		//=======================
    }

    public void onBlockRemoval(World world, int i, int j, int k)
    {
        byte byte0 = 4;
        int l = byte0 + 1;
        if(world.checkChunksExist(i - l, j - l, k - l, i + l, j + l, k + l))
        {
            for(int i1 = -byte0; i1 <= byte0; i1++)
            {
                for(int j1 = -byte0; j1 <= byte0; j1++)
                {
                    for(int k1 = -byte0; k1 <= byte0; k1++)
                    {
                        int l1 = world.getBlockId(i + i1, j + j1, k + k1);
                        if(l1 != Block.leaves.blockID)
                        {
                            continue;
                        }
                        int i2 = world.getBlockMetadata(i + i1, j + j1, k + k1);
                        if((i2 & 8) == 0)
                        {
                            world.setBlockMetadata(i + i1, j + j1, k + k1, i2 | 8);
                        }
                    }

                }

            }

        }
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if(i == 1)
        {
            return 21;
        }
        if(i == 0)
        {
            return 21;
        }
        if(j == 1)
        {
            return 116;
        }
        return j != 2 ? 20 : 117;
    }

    protected int damageDropped(int i)
    {
        return i;
    }
}
