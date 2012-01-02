// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.Random;

//========
// BEGIN NATURE OVERHAUL
//========
import moapi.ModMappedMultiOption;
import moapi.ModBooleanOption;
import moapi.ModOptions;
//========
// END NATURE OVERHAUL
//========

public class BlockCactus extends BlockMortal
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
		// BEGIN NATURE OVERHAUL
		//========
		if(!world.multiplayerWorld) {
			ModOptions cactii = mod_AutoForest.cactii;
			boolean grow = ((ModBooleanOption) cactii.getOption("CactiiGrow")).getValue();
			if(grow) {
				double growthRate = 1D /(((ModMappedMultiOption) cactii
						.getOption("CactiiGrowthRate")).getValue());
				attemptGrowth(world, i, j, k, growthRate);
			}
			
			// ATTEMPT DEATH
			boolean death = mod_AutoForest.cactiiDeath.getValue();
			// 4.5D instead of 1.5D due to reeds being 3 high
			double deathProb = 1D / (4.5D * (((ModMappedMultiOption) cactii
						.getOption("CactiiDeathRate")).getValue()));
			if(death && hasDied(world, i, j, k, deathProb)) {
				death(world, i, j, k);
			}
		}
    }
    protected String growthModifierType = "CactiiSpawn";
	protected String deathModifierName  = "CactiiDeath";
	
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
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
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
        entity.attackEntityFrom(DamageSource.cactus, 1);
    }
}
