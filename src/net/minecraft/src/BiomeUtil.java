package net.minecraft.src;

import java.util.Hashtable;

/**
* A simple API for quickly finding biome data
*
* @author	Clinton Alexander
*/
public class BiomeUtil {
	/**
	* Get a Biome object from a pair of x/z coordinates
	*
	* @param	i	i coord (x)
	* @param	k	k coord (z)
	* @return	Biome object, if name doesn't exist, Biome.UNKNOWN is returned
	*/
	public static BiomeGenBase getBiome(int i, int k) {
		WorldChunkManager cm = ModLoader.getMinecraftInstance().theWorld
										.getWorldChunkManager();
		return cm.getBiomeGenAt(i, k);
	}
}
