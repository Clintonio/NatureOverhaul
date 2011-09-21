package net.minecraft.src;

import java.util.HashMap;

/**
* An enum for abstracting the type of Biome being used
*
* @author	Clinton Alexander
*/
public enum Biome {
	
	SWAMPLAND 		(0),
	FOREST 			(1),
	TAIGA			(2),
	DESERT			(3),
	PLAINS			(4),
	HELL			(5),
	OCEAN			(6),
	HILLS			(7),
	RIVER			(8),
	SKY				(9);
	// Removed: 0, 2, 4, 5, 8, 10
	
	/*	RAINFOREST 		(0),
	SWAMPLAND 		(1),
	SEASONAL_FOREST	(2),
	FOREST 			(3),
	SAVANNA 		(4),
	SHRUBLAND		(5),
	TAIGA			(6),
	DESERT			(7),
	ICE_DESERT		(8),
	PLAINS			(9),
	TUNDRA			(10),
	HELL			(11);*/
	private static HashMap<String, Biome> BiomeTranslate 
									= new HashMap<String, Biome>();
	
	private int i;
	
	Biome(int index) {
		this.i = index;
	}
	
	public int getIndex() {
		return i;
	}
	
	/**
	* Get a Biome object from a string
	*
	* @param	name	Name of Biome
	* @return	Biome object
	*/
	public static Biome getBiomeFromString(String name) {
		if(!BiomeTranslate.containsKey(name)) {
			throw new NullPointerException("No such Biome " + name);
		} else {
			return BiomeTranslate.get(name);
		}
	}
	
	static {
		BiomeTranslate.put("Swampland", Biome.SWAMPLAND);
		BiomeTranslate.put("Forest", Biome.FOREST);
		BiomeTranslate.put("Taiga", Biome.TAIGA);
		BiomeTranslate.put("Desert", Biome.DESERT);
		BiomeTranslate.put("Plains", Biome.PLAINS);
		BiomeTranslate.put("Hell", Biome.HELL);
		BiomeTranslate.put("Ocean", Biome.OCEAN);
		BiomeTranslate.put("Extreme Hills", Biome.HILLS);
		BiomeTranslate.put("River", Biome.RIVER);
		BiomeTranslate.put("Sky", Biome.SKY);
	}
}