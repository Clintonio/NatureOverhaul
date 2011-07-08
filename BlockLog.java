// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.Random;
//=======================
// START NATURE OVERHAUL
//=======================
import modoptionsapi.ModOptionsAPI;
import modoptionsapi.ModOptions;
import modoptionsapi.ModBooleanOption;
import modoptionsapi.ModMappedMultiOption;
import java.util.HashSet;
//=======================
// END NATURE OVERHAUL
//=======================

public class BlockLog extends Block
{
	private static final int MAX_TREE_HEIGHT = 16;
	
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
		return killTree(world,i,j,k,true);
	}
	/**
	* Kills a tree from the given block upwards
	*
	* @param	treeDeath		If true then tree turns into wood
	* @return	Number of logs removed
	*/
	private int killTree(World world, int i, int j, int k, boolean treeDeath) {
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
		flagBlock(block, flags);
		return 1 + scanAndFlag(world, base, block, flags, treeHeight);
	}
	
	
	/**
	* Scans, deletes and flags blocks for lumberjacking
	*
	* @param	block	Current block to scan and flag
	* @param	flags	Flag store
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
			if((inRange(nBlock, base,treeHeight)) && (!isBlockFlagged(nBlock, flags))
				&& ((id == Block.wood.blockID) || (id == Block.leaves.blockID))) {
				flagBlock(nBlock, flags);
				removed = removed + scanAndFlag(world, base, nBlock, flags, treeHeight);
			}
		}
		// Remove the current block if it's a non-tree log
		if((world.getBlockId(i,j,k) == Block.wood.blockID)
			&& ((!isTree(world, i, j, k)) || ((i == base[0]) && (k == base[2])))) {
			killLog(world, block[0], block[1], block[2], true);
			removed++;
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
	private void flagBlock(int[] block, HashSet<Integer> flags) {
		int flag = makeFlag(block);
		
		flags.add(flag);
	}
	
	/**
	* check if block is flagged
	*/
	private boolean isBlockFlagged(int[] block, HashSet<Integer> flags) {
		int flag = makeFlag(block);
		
		return (flags.contains(flag));
	}
	
	/**
	* Create a flag from a block
	*/
	private int makeFlag(int[] block) {
		int iBits = 4;
		int jBits = 6;
		int kBits = 4;
		int i = block[0] % (int) Math.pow(2, iBits);
		int j = block[1] % (int) Math.pow(2, jBits);
		int k = block[2] % (int) Math.pow(2, kBits);
		
		// Put i j+k bits in front so no clash with j/k
		// put j in the jth to kth bits
		// put k in the kth bits.
		// This way we can store i,j,k  as a single int
		// The size of i,j,kBits depends on the distance
		// to search in the x/z of the inRange algoirthm
		int flag = (int) (i << (jBits + kBits)) ^ (j << kBits) ^ k;
		
		return flag;
	}
	
	/**
	* Check if a block is in range of the base
	*/
	private boolean inRange(int[] block, int[] base, int treeHeight) {
		// Small trees have lower limits
		if(treeHeight <= 5) {
			int xDist = 2;
			int yDist = 2;
			int zDist = treeHeight + 2;
		} else {
			int xDist = (int) Math.ceil((2 * treeHeight) / 3);
			int yDist = (int) Math.ceil(treeHeight * 1.75);
			int zDist = (int) Math.ceil((2 * treeHeight) / 3);;
		}
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
		world.entityJoinedWorld(ent);
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
	
    public int idDropped(int i, Random random)
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
	            	int damage = killTree(world, i, j, k, false);
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
