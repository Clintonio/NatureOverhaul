// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.Random;
import moapi.ModOptionsAPI;
import moapi.ModOptions;
import moapi.ModMappedMultiOption;
import moapi.ModBooleanOption;

public class BlockFlower extends BlockMortal
{

    protected BlockFlower(int i, int j)
    {
        super(i, Material.plants);
        blockIndexInTexture = j;
        setTickOnLoad(true);
        float f = 0.2F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 3F, 0.5F + f);
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        return super.canPlaceBlockAt(world, i, j, k) && canThisPlantGrowOnThisBlockID(world.getBlockId(i, j - 1, k));
    }

    protected boolean canThisPlantGrowOnThisBlockID(int i)
    {
        return i == Block.grass.blockID || i == Block.dirt.blockID || i == Block.tilledField.blockID;
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
        super.onNeighborBlockChange(world, i, j, k, l);
        checkFlowerChange(world, i, j, k);
    }

	//========
	// BEGIN NATURE OVERHAUL
	//========
    protected String growthModifierType = "FlowerSpawn";
	
	public void updateTick(World world, int i, int j, int k, Random random) {
		if((!world.multiplayerWorld) && (!(this instanceof BlockCrops))) {
			// ATTEMPT REPRODUCTION
			ModOptions flowers = mod_AutoForest.flowers;
			boolean grow = ((ModBooleanOption) flowers.getOption("FlowersGrow")).getValue();
			if(grow) {
				double growthRate = 1D /(((ModMappedMultiOption) flowers
						.getOption("FlowerGrowthRate")).getValue());
				attemptGrowth(world, i, j, k, growthRate);
			}
			
			// ATTEMPT DEATH
			boolean death = mod_AutoForest.flowerDeath.getValue();
			double deathProb = 1D / (1.5D * (((ModMappedMultiOption) flowers
						.getOption("FlowerDeathRate")).getValue()));
			if(death && hasDied(world, i, j, k, deathProb)) {
				death(world, i, j, k);
			}
		}
		
		if(world.getBlockId(i, j, k) != 0) {
			checkFlowerChange(world, i, j, k);
		}
    }
	//========
	// END NATURE OVERHAUL
	//========
		
    protected final void checkFlowerChange(World world, int i, int j, int k)
    {
        if(!canBlockStay(world, i, j, k))
        {
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlockWithNotify(i, j, k, 0);
        }
    }

    public boolean canBlockStay(World world, int i, int j, int k)
    {
        return (world.getFullBlockLightValue(i, j, k) >= 8 || world.canBlockSeeTheSky(i, j, k)) && canThisPlantGrowOnThisBlockID(world.getBlockId(i, j - 1, k));
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        return null;
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
