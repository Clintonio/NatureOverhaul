package net.minecraft.src;

import net.minecraft.src.modoptionsapi.ModOptionsAPI;
import net.minecraft.src.modoptionsapi.ModOption;
import net.minecraft.src.modoptionsapi.ModBooleanOption;
import net.minecraft.src.modoptionsapi.ModOptions;

import java.util.HashMap;

/**
* AutoForest configuration base, sets up options
*
* @author	Clinton Alexander
* @version	0.7
* @since	0.6
*/
public class mod_AutoForest extends BaseMod {
	// Objects
	private static ModOptions darkerNights;
	private static ModOptions options;
	private static ModOptions climate;
	
	// Constants
	public static final String PLANT_MENU_NAME	 = "Plants Options";
	public static final String SAPLING_MENU_NAME = "Sapling Options";
	public static final String MENU_NAME 		 = "AutoForest"; 
	public static final String TREE_MENU_NAME 	 = "Tree Options";
	public static final String CLIMATE_MENU_NAME = "Climate Options";
	public static final String NIGHT_MENU_NAME	 = "Day and Night Options";
	public static final String FLOWER_MENU_NAME	 = "Flower Options";
	public static final String CACTI_MENU_NAME	 = "Cactus Options";
	public static final String REED_MENU_NAME	 = "Reed Options";
	public static final String PUMPKIN_MENU_NAME = "Pumpkin Options";
	public static final String SHROOMS_MENU_NAME = "Mushroom Options";
	
	
	public static final HashMap<String, byte[]> biomeModifier = new HashMap<String, byte[]>();
	
	/**
	* Version
	*/
	public String Version() {
		return "0.9.6";
	}
	
	/**
	* Initialises and configures all options
	*/
	public mod_AutoForest() {
		createBiomeModifiers();
		setupModOptions();
	}
	
	/**
	* Sets up the mod options for this mod
	*/
	private void setupModOptions() {
		ModOptions ops = new ModOptions(MENU_NAME);
		
		// Options for saplings only
		ModOptions saps = new ModOptions(SAPLING_MENU_NAME);
		
		// Options for plants (shrooms and flowers) only
		ModOptions plants = new ModOptions(PLANT_MENU_NAME);
		
		// Sub options for plants
		ModOptions flowers = new ModOptions(FLOWER_MENU_NAME);
		ModOptions cacti = new ModOptions(CACTI_MENU_NAME);
		ModOptions reed = new ModOptions(REED_MENU_NAME);
		ModOptions pumpkins = new ModOptions(PUMPKIN_MENU_NAME);
		ModOptions shrooms = new ModOptions(SHROOMS_MENU_NAME);
		
		// Options for trees
		ModOptions tree = new ModOptions(TREE_MENU_NAME);
		
		// Options for climate/ biomes
		climate = new ModOptions(CLIMATE_MENU_NAME);
				
		ops.addSubOptions(saps);
		ops.addSubOptions(plants);
		ops.addSubOptions(climate);
		ops.addSubOptions(tree);
		
		// Sapling related
		String[] labels = {"AVERAGE", "FAST", "SUPERFAST", "INSANE", "SUPERSLOW", "SLOW"};
		saps.addToggle("AutoSapling");
		saps.addToggle("FastSapling");
		saps.addToggle("SaplingDeath");
		saps.addMultiOption("GrowthRate", labels);
		
		// Tree options
		Integer[] dKeys = {5000, 2500, 250, 5, 20000, 10000};
		Integer[] keys 		= {5, 3, 1, 0, 9, 7};
		String[]  values 	= {"DEFAULT/AVERAGE", "FAST", "VERY FAST", "INSTANT", "VERY SLOW", "SLOW"};
		tree.addMappedMultiOption("TreeGrowthRate", keys, values);
		tree.addToggle("Lumberjack");
		tree.addMappedMultiOption("DeathRate", dKeys, labels);
		tree.addToggle("TreeDeath");
		
		// Tree drops
		Integer[] aKeys = {3000, 1200, 250, 5, 30000, 10000};
		tree.addMappedMultiOption("CocoaGrowthRate", aKeys, labels);
		tree.addToggle("CocoaGrows");
		tree.addMappedMultiOption("AppleGrowthRate", aKeys, labels);
		tree.addToggle("ApplesGrow");
		
		// Plant related
		Integer[] pKeys 	= {2400, 240, 30, 5, 30000, 9000};
		plants.addSubOptions(flowers);
		plants.addSubOptions(cacti);
		plants.addSubOptions(reed);
		plants.addSubOptions(pumpkins);
		plants.addSubOptions(shrooms);
		
		// Plant submenus
		flowers.addToggle("AutoFlower");
		flowers.addMappedMultiOption("FlowerGrowthRate", pKeys, labels);
		flowers.addToggle("FlowerGrow");
		cacti.addToggle("AutoCacti");
		cacti.addMappedMultiOption("CactiGrowthRate", pKeys, labels);
		cacti.addToggle("CactiGrow");
		reed.addToggle("AutoReed");
		reed.addMappedMultiOption("ReedGrowthRate", pKeys, labels);
		reed.addToggle("ReedGrow");
		pumpkins.addToggle("AutoPumpkin");
		pumpkins.addMappedMultiOption("PumpkinGrowthRate", pKeys, labels);
		pumpkins.addToggle("PumpkinGrow");
		shrooms.addToggle("AutoShroom");
		shrooms.addMappedMultiOption("ShroomGrowthRate", pKeys, labels);
		shrooms.addToggle("ShroomGrow");
		
		// Climate related
		climate.addToggle("BiomeModifiedGrowth");
		addDarkerNights(climate);
		
		// Forest related
		ops.addToggle("ForestGrowth");
		
		// Handle display
		flowers.setWideOption("AutoFlower");
		cacti.setWideOption("AutoCacti");
		reed.setWideOption("AutoReed");
		pumpkins.setWideOption("AutoPumpkin");
		shrooms.setWideOption("AutoShroom");
		tree.setWideOption("TreeGrowthRate");
		ops.setWideOption("ForestGrowth");
		climate.setWideOption("BiomeModifiedGrowth");
		
		// Set the custom sapling item
		Item.itemsList[Block.sapling.blockID] = (new ItemSapling(Block.sapling.blockID - 256)).setItemName("Sapling");
		// Add the two new sapling names
		ModLoader.AddLocalization("birchsapling.name", "Birch Sapling");
		ModLoader.AddLocalization("pinesapling.name", "PineSapling");
		ModLoader.AddLocalization("sapling.name", "Sapling");
		
		ops.loadValues();
		ModOptionsAPI.addMod(ops);
		
		// Set up instancevar
		options = ops;
	}
	
	/**
	* Adds the darker nights mod
	*/
	private void addDarkerNights(ModOptions parent) {
		darkerNights = new ModOptions(NIGHT_MENU_NAME);
		String[] options = {"Bright","Light","Mild","Dim","Dark"};
		Integer[] values = {11,12,13,14,15};
		darkerNights.addMappedMultiOption("Moonlight", values, options);
		darkerNights.addToggle("VaryingMoonlight");
		darkerNights.setOptionValue("VaryingMoonlight", false);
		
		parent.addSubOptions(darkerNights);
	}
	
	/**
	* Return the darker nights object
	*/
	public static ModOptions getDarkerNights() {
		return darkerNights;
	}
	
	/**
	* Returns a specific set of biome modifiers
	*
	* @param	biomeName		Name of biome to get data for
	* @param	name			Type of modifier to get
	* @return	A value of a modifier
	*/
	public static byte getBiomeModifier(String biomeName, String name) {
		byte[] biomeMod = null;
		Biome biome 	= null;
		try {
			Biome.getBiomeFromString(biomeName);
			biome = Biome.getBiomeFromString(biomeName);
			biomeMod = biomeModifier.get(name);
		
			if(biomeMod != null) {
				return biomeMod[biome.getIndex()];
			} else {
				System.out.println("Biome Mod Type missing: " + name);
				return 0;
			}
		} catch (NullPointerException e) {
			System.out.println("Biome missing: " + biomeName);
			return 0;
		}
	}
	
	/**
	* Sets up the biome modifiers
	*/
	private void createBiomeModifiers() {
		byte[] saplingSpawn = { 33,-10, 15,  0,-90,-90, 10,-100,-100,-95,-95,-100 };
		byte[] saplingDeath = {  0, 10,  0,  0, 90, 90, 10,  95,  95, 95, 95, 100 };
		byte[] treeDeath	= {  0,-10,  0,  0,-90,  0,  0, -90, -90,-90,-90, 100 };
		byte[] bigTree		= { 20, 10, 15, 10,  1,  5, 10,   5,   0,  0,  0,   0 };
		byte[] treeGap		= {  0,  3,  1,  1,  5,  4,  2,  10,  10,  9, 10,   0 };
		byte[] flowerSpawn	= { 15, 25, 10, 10,-75, 50,  0,-100,-100,-90, 95,-100 };
		byte[] cactiSpawn	= { 15, 25, 10, 10,-75, 50,  0,-100,-100,-90, 95,-100 };
		byte[] reedSpawn	= { 15, 25, 10, 10,-75, 50,  0,-100,-100,-90, 95,-100 };
		byte[] pumpkinSpawn	= { 15, 25, 10, 10,-75, 50,  0,-100,-100,-90, 95,-100 };
		byte[] shroomSpawn	= { 15, 25, 10, 10,-75, 50,  0,-100,-100,-90, 95,-100 };
		
		biomeModifier.put("SaplingSpawn", saplingSpawn);
		biomeModifier.put("SaplingDeath", saplingDeath);
		biomeModifier.put("TreeDeath", treeDeath);
		biomeModifier.put("BigTree", bigTree);
		biomeModifier.put("TreeGap", treeGap);
		biomeModifier.put("FlowerSpawn", flowerSpawn);
		biomeModifier.put("CactiSpawn", cactiSpawn);
		biomeModifier.put("ReedSpawn", reedSpawn);
		biomeModifier.put("PumpkinSpawn", pumpkinSpawn);
		biomeModifier.put("ShroomSpawn", shroomSpawn);
	}
	
	/**
	* Add biome modifier
	*
	* @throws	ArrayOutOfBoundsException
	* @param	name	Identifier for this modifier, overwrites old ones if same name.
	* @param	byte[]	Array of modifiers. Must be 11 long
	*/
	public void addBiomeModifier(String name, byte[] mods) {
		if(mods.length != 12) {
			throw new IndexOutOfBoundsException("Out of bounds. Length must be 11.");
		} else {
			biomeModifier.put(name, mods);
		}
	}
	
	/**
	* Apply a biome modifier to a value
	*
	* @param	value		original value
	* @param	biomeMod	type of modifier
	* @param	world		world
	* @return	modified int value, round up
	*/
	public static double applyBiomeModifier(double value, String biomeMod, World world, int i, int k) {	
		boolean biomeModsEnabled = ((ModBooleanOption) climate.getOption("BiomeModifiedGrowth")).getValue();
		// Ensure the user wants us to modify by biome
		if(biomeModsEnabled) {
			// Get Biome info
			String name = world.getBiomeName(i,k);
			
			value = (value + (value * 0.01 * getBiomeModifier(name, biomeMod)));
		} 
		
		return value;
	}
}