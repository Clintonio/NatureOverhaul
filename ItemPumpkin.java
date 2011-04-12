package net.minecraft.src;

import net.minecraft.src.modoptionsapi.*;

/**
* Pumpkin Item Representation for pumpkin growth.
*
* @author	Clinton Alexander
* @version	1.0.0.0
*/
public class ItemPumpkin extends ItemBlock implements Plantable {
	
    public ItemPumpkin(int i)
    {
        super(i);
    }

    public int func_21012_a(int i)
    {
        return i;
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
		boolean pumpkinGrow = ((ModBooleanOption) ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME)
				.getSubOption(mod_AutoForest.PLANT_MENU_NAME)
				.getSubOption(mod_AutoForest.PUMPKIN_MENU_NAME)
				.getOption("PumpkinsGrow")).getValue();
		return ((pumpkinGrow) && ((belowID == 2) || (belowID == 3)));
	}
}
