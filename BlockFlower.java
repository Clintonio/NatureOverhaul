package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;
import net.minecraft.src.modoptionsapi.ModOptionsAPI;
import net.minecraft.src.modoptionsapi.ModOptions;
import net.minecraft.src.modoptionsapi.ModMappedMultiOption;
import net.minecraft.src.modoptionsapi.ModBooleanOption;

public class BlockFlower extends Block
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

    public void updateTick(World world, int i, int j, int k, Random random)
    {
		//========
		// BEGIN AUTOFOREST
		//========
		attemptGrowth(world, i, j, k);
		//========
		// END AUTOFOREST
		//========
        func_268_h(world, i, j, k);
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
		
		// average time, in mins, between plant pawning.
		double freq = 1D / (double) growthRate.getValue();
		
		freq = (double) mod_AutoForest.applyBiomeModifier(freq, 
						BiomeMod.FLOWER_SPAWN, world,i,k);
		
		if(plantsGrow.getValue()) {
			if(grown(freq)) {
				System.out.println("(AutoForest): PLANT " + id + " GROWN IN BIOME " + 
				world.getBiomeName(i,k) + ". FREQ: " + freq);
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
	private boolean grown(double freq) {
		// Since tickRate() is 10, we only use 6 as the mult.
		return (Math.random() < freq);
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
