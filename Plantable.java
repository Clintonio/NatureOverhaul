package net.minecraft.src;

/**
* An item that can be planted
*
* @author	Clinton Alexander
* @version	1.0.0.0
*/
public interface Plantable {
	/**
	* Check if the item can be planted on top of the 
	* block with the idBelow at i, j, k
	*
	* @param	idBelow		ID of block below
	* @param	age			Age of item
	* @return 	True when plantable
	*/
	public boolean plantable(World world, int i, int j, int k, int idBelow, int age);
	
	/**
	* Get the block ID of the plantable item
	*
	* @return	Block ID of the plant to make
	*/
	public int getPlantBlockID();
	
	/**
	* Get the velocities of this item when it is created
	*
	* @param	baseSpeed	Base speed of the item
	* @return	Array of speeds in format [x, y, z] velocities
	*/
	public float[] getVelocities(double baseSpeed);
}