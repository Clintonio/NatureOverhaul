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
	* @return True if growth occured
	*/
	protected boolean attemptGrowth(World world, int i, int j, int k) {
		return attemptGrowth(world, i, j, k, getGrowthProb(world, i, j, k));
	}
	
	/**
	* Attempt to grow here
	*
	* @param	prob	Probability of growing from 0 to 1 inclusive
	* @return True if growth occured
	*/
	protected boolean attemptGrowth(World world, int i, int j, int k, float prob) {
		if(growth(prob)) {
			grow(world, i, j, k);
			
			return true;
		}
		
		return false;
	}
	
	/**
	* Get the probability of growth occuring on this block
	*
	* @return	Probability of growth occuring on this tick
	*/
	public abstract float getGrowthProb(World world, int i, int j, int k);
	
	/**
	* Calculate an optimal distance coefficient. This is for cases
	* where a larger number is desired the furhter you get from
	* the optimal value. The minimum is 1, so multiplying any number "r"
	* by the result of this operation will result in "r" if r is equal to "opt".
	* If r is extremely far from opt, the coefficient will be extremely large
	*
	* @param	rain		Current value
	* @param	opt		Optimal value
	* @param	tol		tolerance (lower = Higher probability)
	* @return The modifier. Output always >= 1, where 1 is "just as likely" and
	* 		higher is "less likely"
	*/
	protected float getOptValueMult(float rain, float opt, float tol) {
		float out = tol * (float) Math.pow(opt - rain, 2) + 1;
		
		return out;
	}
	
	/**
	* Check if the plant has grown
	*
	* @param	prob		The probability of growth from 0 to 1
	* @return	True if a plant has possibly grown
	*/
	protected boolean growth(double prob) {
		// Since tickRate() is 10, we only use 6 as the mult.
		return (Math.random() < prob);
	}
	
	/**
	* Grow an item
	*/
	public void grow(World world, int i, int j, int k) {
		int metadata = world.getBlockMetadata(i, j, k);
		int id = idDropped(metadata, world.rand, 0);
		if((id >= 0) && (id < Item.itemsList.length)) {
			// Patch: Some mods add items that have no shifted index
			Item item = Item.itemsList[id];
			ItemStack stack;
			if(item instanceof Item) {
				stack = new ItemStack(Item.itemsList[id]);
			} else {
				stack = new ItemStack(id, 1, 0);
			}
			EntityItem entityitem = new EntityItem(world, i, j, k, stack, true);
			world.spawnEntityInWorld(entityitem);
		}
	}
}
