package net.minecraft.src;

import moapi.ModBooleanOption;
import moapi.ModOptionsAPI;

/**
* Cactus Item Representation for varied cactii
*
* @author	Clinton Alexander
* @version	1.0.0.0
*/
public class ItemCactus extends ItemPlantable {
	
    public ItemCactus(int i)
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
		boolean cactiGrow 	= ((ModBooleanOption) ModOptionsAPI.getModOptions(mod_AutoForest.MENU_NAME)
				.getSubOption(mod_AutoForest.PLANT_MENU_NAME)
				.getSubOption(mod_AutoForest.CACTI_MENU_NAME)
				.getOption("CactiiGrow")).getValue();
		return ((cactiGrow) && (belowID == 12) && (!world.getBlockMaterial(i - 1, j, k).isSolid())
				&& (!world.getBlockMaterial(i + 1, j, k).isSolid()) 
				&& (!world.getBlockMaterial(i, j, k - 1).isSolid())
				&& (!world.getBlockMaterial(i, j, k + 1).isSolid()));
	}
	
	/**
	* Get the velocities of this item when it is created
	*
	* @param	double	Base speed of item
	* @return	Array of speeds in format [x, y, z] velocities
	*/
	public float[] getVelocities(double baseSpeed) {
		float[] out = super.getVelocities(baseSpeed);
		
		out[1] = (float) (baseSpeed + (baseSpeed * Math.random() * 3));
		
		return out;
	}
}
