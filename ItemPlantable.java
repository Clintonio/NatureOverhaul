package net.minecraft.src;

/**
* An abstract plantable item interface implementation
* for item blocks
*
* @author	Clinton Alexander
*/
public abstract class ItemPlantable extends ItemBlock implements Plantable {

	/**
	* To interface with itemblock
	*/
    protected ItemPlantable(int i) {
        super(i);
    }

	/**
	* Get the block ID of the plantable item
	*
	* @return	Block ID of the plant to make
	*/
	public int getPlantBlockID() {
		return shiftedIndex;
	}
	
	/**
	* Check if the item can be planted on top of the 
	* block with the idBelow at i, j, k
	*
	* @param	belowID		ID of block below
	* @param	age			Age of item
	* @return 	True when plantable
	*/
	public abstract boolean plantable(World world, int i, int j, int k, int belowID, int age);
}