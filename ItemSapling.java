package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import net.minecraft.src.modoptionsapi.*;

/**
* Sapling Item Representation for varied saplings.
*
* @author	Clinton Alexander
* @version	1.0.0.0
*/
public class ItemSapling extends ItemPlantable implements Plantable {
	
    public ItemSapling(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int func_21012_a(int i)
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
}
