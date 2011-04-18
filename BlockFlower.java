package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;
import net.minecraft.src.modoptionsapi.ModOptionsAPI;
import net.minecraft.src.modoptionsapi.ModOptions;
import net.minecraft.src.modoptionsapi.ModMappedMultiOption;
import net.minecraft.src.modoptionsapi.ModBooleanOption;

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
        return canThisPlantGrowOnThisBlockID(world.getBlockId(i, j - 1, k));
    }

    protected boolean canThisPlantGrowOnThisBlockID(int i)
    {
        return i == Block.grass.blockID || i == Block.dirt.blockID || i == Block.tilledField.blockID;
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
        super.onNeighborBlockChange(world, i, j, k, l);
        func_268_h(world, i, j, k);
    }

	//========
	// BEGIN AUTOFOREST
	//========
    protected String growthModifierType = "FlowerSpawn";
	protected String deathModifierName  = "StandardDeath";
	
	public void updateTick(World world, int i, int j, int k, Random random) {
		if(!world.multiplayerWorld) {
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
			func_268_h(world, i, j, k);
		}
    }
	//========
	// END AUTOFOREST
	//========
		
    protected final void func_268_h(World world, int i, int j, int k)
    {
        if(!canBlockStay(world, i, j, k))
        {
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
            world.setBlockWithNotify(i, j, k, 0);
        }
    }

    public boolean canBlockStay(World world, int i, int j, int k)
    {
        return (world.getBlockLightValue(i, j, k) >= 8 || world.canBlockSeeTheSky(i, j, k)) && canThisPlantGrowOnThisBlockID(world.getBlockId(i, j - 1, k));
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
