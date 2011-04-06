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
	public static final String PLANT_MENU_NAME	 = "Plant&Mushroom Options";
	public static final String SAPLING_MENU_NAME = "Sapling Options";
	public static final String MENU_NAME 		 = "AutoForest"; 
	public static final String TREE_MENU_NAME 	 = "Tree Options";
	public static final String CLIMATE_MENU_NAME = "Climate Options";
	public static final String NIGHT_MENU_NAME	 = "Day and Night Options";
	
	public static final HashMap<String, byte[]> biomeModifier = new HashMap<String, byte[]>();
	
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
		// Options for trees
		ModOptions tree = new ModOptions(TREE_MENU_NAME);
		// Options for climate/ biomes
		climate = new ModOptions(CLIMATE_MENU_NAME);
		
		climate.setSingleplayerMode(false);
		ops.setMultiplayerMode(true);
		
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
		
		
		Integer[] aKeys = {3000, 1200, 250, 5, 30000, 10000};
		tree.addMappedMultiOption("CocoaGrowthRate", aKeys, labels);
		tree.addToggle("CocoaGrows");
		tree.addMappedMultiOption("AppleGrowthRate", aKeys, labels);
		tree.addToggle("ApplesGrow");
		
		// Plant related
		Integer[] pKeys 	= {2400, 240, 30, 5, 30000, 9000};
		plants.addMappedMultiOption("PlantGrowthRate", pKeys, labels);
		plants.addToggle("PlantsGrow");
		
		// Climate related
		climate.addToggle("BiomeModifiedGrowth");
		addDarkerNights(climate);
		
		// Forest related
		ops.addToggle("ForestGrowth");
		
		// Handle display
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
	* @param	index			Type of modifier to get
	* @return	A value of a modifier
	*/
	public static byte getBiomeModifier(String biomeName, BiomeMod index) {
		byte[] biomeMod = biomeModifier.get(biomeName);
		
		if(biomeMod != null) {
			return biomeMod[index.getIndex()];
		} else {
			System.out.println("Biome missing: " + biomeName);
			return 0;
		}
	}
	
	/**
	* Sets up the biome modifiers
	*/
	private void createBiomeModifiers() {
		byte[] tmp0 = {  33,  0,  0,  20,  0,  15};
		biomeModifier.put("Rainforest", 	tmp0);
		byte[] tmp1 = { -10, 10,-10,  10,  3,  25};
		biomeModifier.put("Swampland", 			tmp1);
		byte[] tmp2 = {  15,  0,  0,  15,  1,  10};
		biomeModifier.put("Seasonal Forest",tmp2);
		byte[] tmp3 = {   0,  0,  0,  10,  1,  10};
		biomeModifier.put("Forest",			tmp3);
		byte[] tmp4 = { -90, 90,-90,   1,  5, -75};
		biomeModifier.put("Savanna",		tmp4);
		byte[] tmp5 = { -90, 90,  0,   5,  4,  50};
		biomeModifier.put("Shrubland",		tmp5);
		byte[] tmp6 = {  10, 10,  0,  10,  2,   0};
		biomeModifier.put("Taiga",			tmp6);
		byte[] tmp7 = {-100, 95,-90,   5, 10,-100};
		biomeModifier.put("Desert",			tmp7);
		byte[] tmp8 = {-100, 95,-90,   0, 10,-100};
		biomeModifier.put("Ice Desert",		tmp8);
		byte[] tmp9 = { -95, 95,-90,   0,  9, -90};
		biomeModifier.put("Plains",			tmp9);
		byte[] tmp10= { -95, 95,-90,   0, 10, -95};
		biomeModifier.put("Tundra",			tmp10);
	}
	
	/**
	* Apply a biome modifier to a value
	*
	* @param	value		original value
	* @param	biomeMod	type of modifier
	* @param	world		world
	* @return	modified int value, round up
	*/
	public static double applyBiomeModifier(double value, BiomeMod biomeMod, World world, int i, int k) {	
		boolean biomeModsEnabled = ((ModBooleanOption) climate.getOption("BiomeModifiedGrowth")).getValue();
		// Ensure the user wants us to modify by biome
		if(biomeModsEnabled) {
			// Get Biome info
			String name = world.getBiomeName(i,k);
			
			value = (value + (value * 0.01 * getBiomeModifier(name, biomeMod)));
		} 
		
		return value;
	}
	
	/**
	* Version
	*/
	public String Version() {
		return "Beta 1.4";
	}
}