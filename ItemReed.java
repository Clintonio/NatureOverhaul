// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

//========
// START AUTOFOREST
//========
import modoptionsapi.ModOptionsAPI;
import modoptionsapi.ModBooleanOption;
//========
// END AUTOFOREST
//========

public class ItemReed extends Item implements Plantable
{

    public ItemReed(int i, Block block)
    {
        super(i);
        field_320_a = block.blockID;
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
        if(world.getBlockId(i, j, k) == Block.snow.blockID)
        {
            l = 0;
        } else
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
        if(itemstack.stackSize == 0)
        {
            return false;
        }
        if(world.canBlockBePlacedAt(field_320_a, i, j, k, false, l))
        {
            Block block = Block.blocksList[field_320_a];
            if(world.setBlockWithNotify(i, j, k, field_320_a))
            {
                Block.blocksList[field_320_a].onBlockPlaced(world, i, j, k, l);
                Block.blocksList[field_320_a].onBlockPlacedBy(world, i, j, k, entityplayer);
                world.playSoundEffect((float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, block.stepSound.func_1145_d(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                itemstack.stackSize--;
            }
        }
        return true;
    }
	
	//========
	// START AUTOFOREST
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
	* Get plant ID
	*
	* @return	ID of block to plant
	*/
	public int getPlantBlockID() {
		return field_320_a;
	}
	//========
	// END AUTOFOREST
	//========

    private int field_320_a;
}
