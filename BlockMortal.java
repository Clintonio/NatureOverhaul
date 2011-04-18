package net.minecraft.src;

/**
* Specifies a block which is mortal and can die
* (not in the sense of sentient, but the sense of
* is not forever like most blocks)
* In this implementation, all blocks that can die can
* also grow
*
* @author	Clinton Alexander
*/
public abstract class BlockMortal extends BlockGrowable implements BlockDeathInterface {
	/**
	* Implement this string as your biome modifier
	* to be used by mod_autoforest.
	* Use empty string to skip biome modification
	*/
	protected String deathModifierName = "StandardDeath";
	
	/**
	* Constructor to interface subblocks with block growable
	*/
    protected BlockMortal(int i, Material material) {
		super(i, material);
	}
	/**
	* See parent constructor
	*/
    protected BlockMortal(int i, int j, Material material) {
		super(i, j, material);
	}
	
	/**
	* Check whether this block has died on this tick for any 
	* reason
	*
	* @param	world
	* @param	i
	* @param	j
	* @param	k
	* @param	prob		Probability of a random death
	* @return	True if plant has died
	*/
	public boolean hasDied(World world, int i, int j, int k, double prob) {
		return (hasStarved(world, i, j, k) || hasRandomlyDied(world, i, j, k, prob));
	}
	
	/**
	* Checks whether this block has starved on this tick
	* by being surrounded by too many of it's kin
	*
	* @param	world
	* @param	i
	* @param	j
	* @param	k
	* @return	True if plant has starved
	*/
	public boolean hasStarved(World world, int i, int j, int k) {
		int radius 			= getPrivacyRadius(world, i, j, k);
		int maxNeighbours 	= getMaxNeighbours(world, i, j, k);
		int foundNeighbours = 0;
		
		if((radius > 0) && (radius < 10)) {
			for(int x = i - radius; x <= i + radius; x++) {
				for(int y = j - radius; y < j + radius; y++) {
					for(int z = k - radius; z < k + radius; z++) {
						if((i != x) || (j != y) || (k != z)) {
							int blockID = world.getBlockId(x, y, z);
							if(blockID == this.blockID) {
								foundNeighbours++;
							}
						}
					}
				}
			}
		}
		
		return (foundNeighbours > maxNeighbours);
	}
	
	/**
	* Return's max radii of other nearby plants
	* Do not use a radius above 5, if you want to, overload hasStarved
	* 5 * 5 * 5 = 125, that's a lot of blocks to check
	* Return 0 to skip this check
	*
	* @param	world
	* @param	i
	* @param	j
	* @param	k
	* @return	Radius of nearby scan to check in starvation routine
	*/
	protected int getPrivacyRadius(World world, int i, int j, int k) {
		return 0;
	}
	
	/**
	* Returns the number of neighbours allowed within this plants privacy 
	* radius. If radius is 0, then this is irrelevant
	*
	* @param	world
	* @param	i
	* @param	j
	* @param	k
	* @return	Number of neighbours within privacy radius
	*/
	protected int getMaxNeighbours(World world, int i, int j, int k) { 
		return 9;
	}
	
	/**
	* Checks whether this block has died from natural random causes
	* 
	* @param	world
	* @param	i
	* @param	j
	* @param	k
	* @param	prob	Probability of death this tick
	* @return	True if plant has randomly died
	*/
	public boolean hasRandomlyDied(World world, int i, int j, int k, double prob) {
		prob = mod_AutoForest.applyBiomeModifier(prob, deathModifierName, world, i, k);
		return (Math.random() < prob);
	}
	
	/**
	* The action to take upon death
	*
	* @param	world
	* @param	i
	* @param	j
	* @param	k
	*/
	public void death(World world, int i, int j, int k) {
		world.setBlockWithNotify(i, j, k, 0);
	}
}