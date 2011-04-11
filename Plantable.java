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
	* @return 	True when plantable
	*/
	public plantable(World world, int i, int j, int k, int idBelow);
}