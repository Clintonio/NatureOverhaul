package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

//========
// BEGIN AUTOFOREST
//========
import java.util.Random;
import net.minecraft.src.modoptionsapi.*;
//========
// END AUTOFOREST
//========

public class BlockPumpkin extends Block
{

    protected BlockPumpkin(int i, int j, boolean flag)
    {
        super(i, Material.pumpkin);
        blockIndexInTexture = j;
        setTickOnLoad(true);
        blockType = flag;
    }
	
	//========
	// BEGIN AUTOFOREST
	//========
    public void updateTick(World world, int i, int j, int k, Random random) {
		if(!world.multiplayerWorld) {
			attemptGrowth(world, i, j, k);
		}
	}
	
	/**
	* Attempt to grow the plant
	*/
	private void attemptGrowth(World world, int i, int j, int k) {
		ModOptions plants = ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME)
			.getSubOption(mod_AutoForest.PLANT_MENU_NAME);
		ModBooleanOption plantsGrow = (ModBooleanOption) plants.getOption("PlantsGrow");
		ModMappedMultiOption growthRate = (ModMappedMultiOption) plants.getOption("PlantGrowthRate");
		
		// Only wild pumpkins grow, not laterns.
		// Blocktype is true if lantern
		if((plantsGrow.getValue()) && (!blockType)){
			// Pumpkins grow 10 times slower
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
		int freq = growthRate.getValue() * 25; // * 25 for 25 times less for pumpkin
		return ((Math.floor(Math.random() * freq) + 1) > (freq - 1));
	}
	//========
	// END AUTOFOREST
	//========
	
    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if(i == 1)
        {
            return blockIndexInTexture;
        }
        if(i == 0)
        {
            return blockIndexInTexture;
        }
        int k = blockIndexInTexture + 1 + 16;
        if(blockType)
        {
            k++;
        }
        if(j == 0 && i == 2)
        {
            return k;
        }
        if(j == 1 && i == 5)
        {
            return k;
        }
        if(j == 2 && i == 3)
        {
            return k;
        }
        if(j == 3 && i == 4)
        {
            return k;
        } else
        {
            return blockIndexInTexture + 16;
        }
    }

    public int getBlockTextureFromSide(int i)
    {
        if(i == 1)
        {
            return blockIndexInTexture;
        }
        if(i == 0)
        {
            return blockIndexInTexture;
        }
        if(i == 3)
        {
            return blockIndexInTexture + 1 + 16;
        } else
        {
            return blockIndexInTexture + 16;
        }
    }

    public void onBlockAdded(World world, int i, int j, int k)
    {
        super.onBlockAdded(world, i, j, k);
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        int l = world.getBlockId(i, j, k);
        return (l == 0 || Block.blocksList[l].blockMaterial.getIsLiquid()) && world.isBlockOpaqueCube(i, j - 1, k);
    }

    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving)
    {
        int l = MathHelper.floor_double((double)((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(i, j, k, l);
    }

    private boolean blockType;
}
