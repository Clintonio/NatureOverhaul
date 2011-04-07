package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;
import net.minecraft.src.modoptionsapi.*;

public class BlockReed extends Block
{

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
		// BEGIN AUTOFOREST
		//========
		if(!world.multiplayerWorld) {
			attemptGrowth(world, i, j, k);
		}
		//========
		// END AUTOFOREST
		//========
    }
	
	//========
	// BEGIN AUTOFOREST
	//========
	/**
	* Attempt to grow the plant
	*/
	private void attemptGrowth(World world, int i, int j, int k) {
		ModOptions plants = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME)
			.getSubOption(mod_AutoForest.PLANT_MENU_NAME);
		ModBooleanOption plantsGrow = (ModBooleanOption) plants.getOption("PlantsGrow");
		ModMappedMultiOption growthRate = (ModMappedMultiOption) plants.getOption("PlantGrowthRate");
		// We need the block ID.
		int id = world.getBlockId(i,j,k);
		
		if(plantsGrow.getValue()) {
			if(grown(growthRate)) {
				grow(world, i, j, k);
			}
		}
	}
	
	/**
	* Cause reed to emit a new reed item
	*/
	public void grow(World world, int i, int j, int k) {
		EntityItem entityitem = new EntityItem(world, i, j, k, 
											   new ItemStack(Item.reed));
		world.entityJoinedWorld(entityitem);
	}
	
	/**
	* Check if the plant has grown
	*
	* @param	growthRate	Configured rate of growth
	* @return	True if a plant has possibly grown
	*/
	private boolean grown(ModMappedMultiOption growthRate) {
		// average time, in mins, between plant pawning.
		int freq = growthRate.getValue() * 5; // * 5 for 5 times less for reeds
		// Since tickRate() is 10, we only use 6 as the mult.
		return ((Math.floor(Math.random() * freq) + 1) > (freq - 1));
	}
	//========
	// END AUTOFOREST
	//========

    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        int l = world.getBlockId(i, j - 1, k);
        if(l == blockID)
        {
            return true;
        }
        if(l != Block.grass.blockID && l != Block.dirt.blockID)
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
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
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

    public int idDropped(int i, Random random)
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
