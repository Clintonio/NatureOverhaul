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
	* Get the velocities of this item when it is created
	*
	* @param	baseSpeed	Base speed of item
	* @return	Array of speeds in format [x, y, z] velocities
	*/
	public float[] getVelocities(double baseSpeed) {
		float[] out = new float[3];
		
		out[0] = (float) (Math.random() * baseSpeed) * randSign();
		out[1] = (float) (baseSpeed + (baseSpeed * Math.random() * 1.5));
		out[2] = (float) (Math.random() * baseSpeed) * randSign();
		
		return out;
	}
	
	/**
	* Picks a random -1 or 1
	*/
	protected int randSign() {
		return (int) Math.pow(-1, (int) Math.round(Math.random()) + 1) * 2;
	}
	
	/**
	* The method by which the item plants itself
	*
	* @param	damage	Item damage
	*/
	public void plant(World world, int i, int j, int k, int damage) {
		world.setBlockAndMetadataWithNotify(i, j, k, getPlantBlockID(), damage);
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
