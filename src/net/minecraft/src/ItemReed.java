// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

//========
// START AUTOFOREST
//========
import moapi.ModOptionsAPI;
import moapi.ModBooleanOption;
//========
// END AUTOFOREST
//========

public class ItemReed extends Item implements Plantable
{

    private int spawnID;

    public ItemReed(int i, Block block)
    {
        super(i);
        spawnID = block.blockID;
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
        int i1 = world.getBlockId(i, j, k);
        if(i1 == Block.snow.blockID)
        {
            l = 0;
        } else
        if(i1 != Block.vine.blockID)
        {
            if(l == 0)
            {
                j--;
            }
            if(l == 1)
            {
                j++;
            }
            if(l == 2)
            {
                k--;
            }
            if(l == 3)
            {
                k++;
            }
            if(l == 4)
            {
                i--;
            }
            if(l == 5)
            {
                i++;
            }
        }
        if(!entityplayer.canPlayerEdit(i, j, k))
        {
            return false;
        }
        if(itemstack.stackSize == 0)
        {
            return false;
        }
        if(world.canBlockBePlacedAt(spawnID, i, j, k, false, l))
        {
            Block block = Block.blocksList[spawnID];
            if(world.setBlockWithNotify(i, j, k, spawnID))
            {
                if(world.getBlockId(i, j, k) == spawnID)
                {
                    Block.blocksList[spawnID].onBlockPlaced(world, i, j, k, l);
                    Block.blocksList[spawnID].onBlockPlacedBy(world, i, j, k, entityplayer);
                }
                world.playSoundEffect((float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, block.stepSound.stepSoundDir2(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                itemstack.stackSize--;
            }
        }
        return true;
    }
	
	//========
	// START NATURE OVERHAUL
	//========
	/**
	* Check if the item can be planted on top of the 
	* block with the idBelow at i, j, k
	*
	* @param	belowID		ID of block below
	* @param	age			Age of item
	* @return 	True when plantable
	*/
	public boolean plantable(World world, int i, int j, int k, int belowID, int age) {
		boolean reedGrow 	= ((ModBooleanOption) ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME)
				.getSubOption(mod_AutoForest.PLANT_MENU_NAME)
				.getSubOption(mod_AutoForest.REED_MENU_NAME)
				.getOption("ReedsGrow")).getValue();
		return ((reedGrow) && (((belowID == 2) || (belowID == 3)) 
				 && ((world.getBlockMaterial(i - 1, j - 1, k) == Material.water) 
				  || (world.getBlockMaterial(i + 1, j - 1, k) == Material.water) 
				  || (world.getBlockMaterial(i, j - 1, k - 1) == Material.water) 
				  || (world.getBlockMaterial(i, j - 1, k + 1) == Material.water))));
	}
	
	/**
	* The method by which the item plants itself
	*
	* @param	damage	Item damage
	*/
	public void plant(World world, int i, int j, int k, int damage) {
		world.setBlockAndMetadataWithNotify(i, j, k, getPlantBlockID(), damage);
	}
	
	/**
	* Get plant ID
	*
	* @return	ID of block to plant
	*/
	public int getPlantBlockID() {
		return spawnID;
	}
	
	/**
	* Get the velocities of this item when it is created
	*
	* @param	baseSpeed	Base speed of item
	* @return	Array of speeds in format [x, y, z] velocities
	*/
	public float[] getVelocities(double baseSpeed) {
		float[] out = new float[3];
		
		out[0] = (float) ((Math.random() * baseSpeed) * randSign());
		out[1] = (float) (baseSpeed + (baseSpeed * Math.random() * 1.5));
		out[2] = (float) ((Math.random() * baseSpeed) * randSign());
		
		return out;
	}
	
	/**
	* Picks a random -1 or 1
	*/
	private int randSign() {
		return (int) Math.pow(-1, (int) Math.round(Math.random()) + 1) * 2;
	}
	//========
	// END NATURE OVERHAUL
	//========
}
