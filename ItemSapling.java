// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

//========
// START AUTOFOREST
//========
import moapi.ModOptionsAPI;
import moapi.ModBooleanOption;
//========
// END AUTOFOREST
//========

public class ItemSapling extends ItemPlantable {

    public ItemSapling(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getPlacedBlockMetadata(int i)
    {
        return i;
    }

    public int getIconFromDamage(int i)
    {
        return Block.sapling.getBlockTextureFromSideAndMetadata(0, i);
    }
	
	/**
	* Check if the item can be planted on top of the 
	* block with the idBelow at i, j, k
	*
	* @param	belowID		ID of block below
	* @param	age			Age of item
	* @return 	True when plantable
	*/
	public boolean plantable(World world, int i, int j, int k, int belowID, int age) {
		boolean saplingGrow = ((ModBooleanOption) ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME).
				getSubOption(mod_AutoForest.SAPLING_MENU_NAME).
				getOption("AutoSapling")).getValue();
		return ((saplingGrow) && ((belowID == 2) || (belowID == 3)));
	}
	
	/**
	* Get the velocities of this item when it is created
	*
	* @param	double	Base speed of item
	* @return	Array of speeds in format [x, y, z] velocities
	*/
	public float[] getVelocities(double baseSpeed) {
		float[] out = super.getVelocities(baseSpeed);
		
		out[1] = (float) (baseSpeed + (baseSpeed * Math.random() * 2));
		
		return out;
	}
}
