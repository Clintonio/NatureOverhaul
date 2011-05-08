package net.minecraft.src;

import java.util.HashMap;

/**
* An implementation of a standard growable block
*
* @author	Clinton Alexander
* @version	1.0.0.0
*/
public abstract class BlockGrowable extends Block implements Growable {
	/**
	* Override this with the name of biome modifier you use
	*/
	protected String growthModifierType = null;
	
	/**
	* See parent constructor
	*/
    protected BlockGrowable(int i, Material material) {
		super(i, material);
	}
	/**
	* See parent constructor
	*/
    protected BlockGrowable(int i, int j, Material material) {
		super(i, j, material);
	}
	
	/**
	* Attempt to grow here
	*
	* @param	prob	Probability of growing from 0 to 1 inclusive
	*/
	protected boolean attemptGrowth(World world, int i, int j, int k, double prob) {
		// Apply a modifier if the user has set one
		if(growthModifierType != null) {
			prob = mod_AutoForest.applyBiomeModifier(prob, growthModifierType, world, i, k);
		}
		
		if(growth(prob)) {
			grow(world, i, j, k);
			
			return true;
		}
		
		return false;
	}
	
	/**
	* Check if the plant has grown
	*
	* @param	growthRate	Configured rate of growth
	* @return	True if a plant has possibly grown
	*/
	protected boolean growth(double freq) {
		// Since tickRate() is 10, we only use 6 as the mult.
		return (Math.random() < freq);
	}
	
	/**
	* Grow an item
	*/
	public void grow(World world, int i, int j, int k) {
		int metadata = world.getBlockMetadata(i, j, k);
		int id = idDropped(metadata, world.rand);
		if((id >= 0) && (id < Item.itemsList.length)) {
			ItemStack itemStack = new ItemStack(Item.itemsList[id]);
			EntityItem entityitem = new EntityItem(world, i, j, k, itemStack, true);
			world.entityJoinedWorld(entityitem);
		}
	}
}