package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;

//========
// BEGIN AUTOFOREST
//========
import net.minecraft.src.modoptionsapi.*;
//========
// END AUTOFOREST
//========

public class BlockCactus extends Block
{

    protected BlockCactus(int i, int j)
    {
        super(i, j, Material.cactus);
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
				//System.out.println("(AutoForest): PLANT " + id + " GROWN");
				// Create the new plan and emit it, let the entity 
				// code handle the rest
				EntityItem entityitem = new EntityItem(world, i, j, k, 
													   new ItemStack(this));
				world.entityJoinedWorld(entityitem);
			}
		}
	}
	
	/**
	* Check if the plant has grown
	*
	* @param	growthRate	Configured rate of growth
	* @return	True if a plant has possibly grown
	*/
	private boolean grown(ModMappedMultiOption growthRate) {
		// average time, in mins, between plant pawning.
		int freq = growthRate.getValue() * 5; // * 5 for 5 times less for cactii
		// Since tickRate() is 10, we only use 6 as the mult.
		return ((Math.floor(Math.random() * freq) + 1) > (freq - 1));
	}
	//========
	// END AUTOFOREST
	//========

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        float f = 0.0625F;
        return AxisAlignedBB.getBoundingBoxFromPool((float)i + f, j, (float)k + f, (float)(i + 1) - f, (float)(j + 1) - f, (float)(k + 1) - f);
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k)
    {
        float f = 0.0625F;
        return AxisAlignedBB.getBoundingBoxFromPool((float)i + f, j, (float)k + f, (float)(i + 1) - f, j + 1, (float)(k + 1) - f);
    }

    public int getBlockTextureFromSide(int i)
    {
        if(i == 1)
        {
            return blockIndexInTexture - 1;
        }
        if(i == 0)
        {
            return blockIndexInTexture + 1;
        } else
        {
            return blockIndexInTexture;
        }
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public int getRenderType()
    {
        return 13;
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        if(!super.canPlaceBlockAt(world, i, j, k))
        {
            return false;
        } else
        {
            return canBlockStay(world, i, j, k);
        }
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
        if(!canBlockStay(world, i, j, k))
        {
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
            world.setBlockWithNotify(i, j, k, 0);
        }
    }

    public boolean canBlockStay(World world, int i, int j, int k)
    {
        if(world.getBlockMaterial(i - 1, j, k).isSolid())
        {
            return false;
        }
        if(world.getBlockMaterial(i + 1, j, k).isSolid())
        {
            return false;
        }
        if(world.getBlockMaterial(i, j, k - 1).isSolid())
        {
            return false;
        }
        if(world.getBlockMaterial(i, j, k + 1).isSolid())
        {
            return false;
        } else
        {
            int l = world.getBlockId(i, j - 1, k);
            return l == Block.cactus.blockID || l == Block.sand.blockID;
        }
    }

    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity)
    {
        entity.attackEntityFrom(null, 1);
    }
}
