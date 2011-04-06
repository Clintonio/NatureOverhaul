package net.minecraft.src;

/**
* An enum for abstracting the type of modifier being used
*
* @author	Clinton Alexander
*/
public enum BiomeMod {
	SAPLING_SPAWN (0),
	SAPLING_DEATH (1),
	// -ve = highr death rates
	TREE_DEATH    (2),
	// out of 100
	BIG_TREE      (3),
	TREE_GAP      (4),
	// +ve = higher growth rates
	FLOWER_SPAWN  (5);
	
	private int i;
	
	BiomeMod(int index) {
		this.i = index;
	}
	
	public int getIndex() {
		return i;
	}
}