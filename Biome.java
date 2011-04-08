package net.minecraft.src;

import java.util.HashMap;

/**
* An enum for abstracting the type of biome being used
*
* @author	Clinton Alexander
*/
public enum Biome {
	
	RAINFOREST 		(0),
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
	HELL			(11);
	
	private static HashMap<String, Biome> biomeTranslate 
									= new HashMap<String, Biome>();
	
	private int i;
	
	Biome(int index) {
		this.i = index;
	}
	
	public int getIndex() {
		return i;
	}
	
	/**
	* Get a biome object from a string
	*
	* @param	name	Name of biome
	* @return	Biome object
	*/
	public static Biome getBiomeFromString(String name) {
		if(!biomeTranslate.containsKey(name)) {
			throw new NullPointerException("No such biome " + name);
		} else {
			return biomeTranslate.get(name);
		}
	}
	
	static {
		biomeTranslate.put("Rainforest", Biome.RAINFOREST);
		biomeTranslate.put("Swampland", Biome.SWAMPLAND);
		biomeTranslate.put("Seasonal Forest", Biome.SEASONAL_FOREST);
		biomeTranslate.put("Forest", Biome.FOREST);
		biomeTranslate.put("Savanna", Biome.SAVANNA);
		biomeTranslate.put("Shrubland", Biome.SHRUBLAND);
		biomeTranslate.put("Taiga", Biome.TAIGA);
		biomeTranslate.put("Desert", Biome.DESERT);
		biomeTranslate.put("Ice Desert", Biome.ICE_DESERT);
		biomeTranslate.put("Plains", Biome.PLAINS);
		biomeTranslate.put("Tundra", Biome.TUNDRA);
		biomeTranslate.put("Hell", Biome.HELL);
	}
}