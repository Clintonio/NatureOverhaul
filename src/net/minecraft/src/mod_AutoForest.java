package net.minecraft.src;

import moapi.ModOptionsAPI;
import moapi.ModOption;
import moapi.ModBooleanOption;
import moapi.ModOptions;
import moapi.ModMappedOption;
import moapi.ModMappedMultiOption;

import java.util.HashMap;

/**
* AutoForest configuration base, sets up options
*
* @author	Clinton Alexander
* @version	0.7
* @since	0.6
*/
public class mod_AutoForest extends BaseMod {
	// Constants
	public static final String PLANT_MENU_NAME	 = "Plants Options";
	public static final String SAPLING_MENU_NAME = "Sapling Options";
	public static final String MENU_NAME 		 = "NatureOverhaul"; 
	public static final String TREE_MENU_NAME 	 = "Tree Options";
	public static final String CLIMATE_MENU_NAME = "Climate Options";
	public static final String NIGHT_MENU_NAME	 = "Day and Night Options";
	public static final String FLOWER_MENU_NAME	 = "Flower Options";
	public static final String CACTI_MENU_NAME	 = "Cactus Options";
	public static final String REED_MENU_NAME	 = "Reed Options";
	public static final String SHROOMS_MENU_NAME = "Mushroom Options";
	private static final String GRASS_MENU_NAME  = "Grass Options";
	private static final String MISC_MENU_NAME  = "Misc Options";
	
	// General option menus
	
	public static ModOptions options = new ModOptions(MENU_NAME);
	
	// Options for saplings only
	public static ModOptions saps = new ModOptions(SAPLING_MENU_NAME);
	
	// Options for trees
	public static ModOptions tree = new ModOptions(TREE_MENU_NAME);
	public static ModBooleanOption leafDecay = new ModBooleanOption("QuickLeafDecay");
	
	// Options for plants
	public static ModOptions 		plants		= new ModOptions(PLANT_MENU_NAME);
	public static ModOptions 		flowers 	= new ModOptions(FLOWER_MENU_NAME);
	public static ModOptions 		cactii 		= new ModOptions(CACTI_MENU_NAME);
	public static ModOptions 		reed 		= new ModOptions(REED_MENU_NAME);
	public static ModOptions 		shrooms 	= new ModOptions(SHROOMS_MENU_NAME);
	public static ModOptions		grass		= new ModOptions(GRASS_MENU_NAME);
	public static ModOptions		misc		= new ModOptions(MISC_MENU_NAME);
	
	public static ModBooleanOption flowerDeath 	= new ModBooleanOption("Flowers Die");
	public static ModBooleanOption shroomDeath 	= new ModBooleanOption("Shrooms Die");
	public static ModBooleanOption reedDeath 	= new ModBooleanOption("Reeds Die");
	public static ModBooleanOption cactiiDeath 	= new ModBooleanOption("Cactii Die");
	public static ModBooleanOption grassDeath 	= new ModBooleanOption("Grass Dies");
	
	// Options for shrooms
	public static ModBooleanOption shroomTreesGrow = new ModBooleanOption("ShroomsTreesGrow");
	public static ModMappedOption  shroomTreeGrowth;
	
	// Options for saplings
	public static ModMappedMultiOption 	growthType;
	public static ModBooleanOption		defaultShroomSpread = new ModBooleanOption("Enable Default Spread");
	
	// Options for misc
	public static ModBooleanOption		mossGrows		= new ModBooleanOption("Moss Grows");
	public static ModMappedMultiOption		mossGrowthRate;
	public static ModBooleanOption		infiniteFire	= new ModBooleanOption("Infinite Fire Spread");
	public static ModBooleanOption 		waterFix		= new ModBooleanOption("Fix Water Bug");
	public static ModMappedMultiOption		reproductionRate;
	
	// Objects
	public static ModOptions climate		= new ModOptions(CLIMATE_MENU_NAME);
	
	// Default labels
	private static String[] labels = {"AVERAGE", "FAST", 
									  "SUPERFAST", "INSANE", 
									  "SUPERSLOW", "SLOW"};
	
	
	private static final HashMap<String, byte[]> biomeModifier = new HashMap<String, byte[]>();
	
	/**
	* Version
	*/
	public String getVersion() {
		return "14";
	}
	
	public mod_AutoForest() {
	
	}
	
	//=====================
	// SETUP METHODS
	//=====================
	
	
	/**
	* Initialises and configures all options (called by ML)
	*/
	public void load() {
		createbiomeModifiers();
		setupModOptions();
	}
	
	/**
	* Sets up the mod options for this mod
	*/
	private void setupModOptions() {
		options.addSubOptions(saps);
		options.addSubOptions(plants);
		options.addSubOptions(climate);
		options.addSubOptions(tree);
		options.addSubOptions(shrooms);
		options.addSubOptions(misc);
		
		// Sapling related
		setupSaplings();
		// Tree options
		setupTreeOptions();
		// Set up flower options
		addFlowers();
		// Set up shrooms
		setupShroomOptions();
		
		// Climate related
		climate.addToggle("BiomeModifiedGrowth");
		climate.setWideOption("biomeModifiedGrowth");
		
		// Add misc options
		addMiscOptions();
		// Set the custom items
		setupItemsAndBlocks();
		
		options.loadValues();
		ModOptionsAPI.addMod(options);
	}
	
	/**
	* Setup sapling options
	*/
	private void setupSaplings() {
		String[] 	growthLabels = {"Both", "Decay", "Growth", "Neither"};
		Integer[]	growthValues = {3, 2, 1, 0};
		
		saps.addToggle("AutoSapling");
		saps.addToggle("SaplingDeath");
		saps.addMultiOption("GrowthRate", labels);
		saps.addMappedMultiOption("Growth Occurs On", growthValues, 
								  growthLabels);
		growthType = (ModMappedMultiOption) saps.getOption("Growth Occurs On");
		
		saps.setWideOption("Growth Occurs On");
	}
	
	/**
	* Setup tree options
	*/
	private void setupTreeOptions() {
		Integer[] dKeys 	= {5000, 2500, 250, 5, 20000, 10000};
		Integer[] keys 		= {5, 3, 1, 0, 9, 7};
		String[]  values 	= {"DEFAULT/AVERAGE", "FAST", "VERY FAST", "INSTANT", "VERY SLOW", "SLOW"};
		
		tree.addMappedMultiOption("TreeGrowthRate", keys, values);
		tree.addOption(leafDecay);
		tree.addToggle("Lumberjack");
		tree.addMappedMultiOption("DeathRate", dKeys, labels);
		tree.addToggle("TreeDeath");
		
		// Tree droptions
		Integer[] aKeys = {3000, 1200, 250, 5, 30000, 10000};
		tree.addMappedMultiOption("CocoaGrowthRate", aKeys, labels);
		tree.addToggle("CocoaGrows");
		tree.addMappedMultiOption("AppleGrowthRate", aKeys, labels);
		tree.addToggle("ApplesGrow");
		
		tree.setWideOption("TreeGrowthRate");
	}
	
	/**
	* Setup shroom options
	*/
	private void setupShroomOptions() {
		// Plant related
		Integer[] pKeys 	= {2400, 240, 30, 5, 30000, 9000};
		
		shroomTreeGrowth = new ModMappedOption("ShroomTreeGrowthRate", pKeys, labels);
		
		shrooms.addOption(defaultShroomSpread);
		shrooms.addOption(shroomTreeGrowth);
		shrooms.addOption(shroomTreesGrow);
		shrooms.addMappedMultiOption("ShroomDeathRate", pKeys, labels);
		shrooms.addOption(shroomDeath);
		shrooms.addMappedMultiOption("ShroomGrowthRate", pKeys, labels);
		shrooms.addToggle("ShroomsGrow");
		
		defaultShroomSpread.setValue(false);
	}
	
	/**
	* Set up flower options
	*/
	private void addFlowers() {
		// Plant related
		Integer[] pKeys 	= {2400, 240, 30, 5, 30000, 9000};
		plants.addSubOptions(flowers);
		plants.addSubOptions(cactii);
		plants.addSubOptions(reed);
		plants.addSubOptions(grass);
		
		// Plant submenus
		flowers.addMappedMultiOption("FlowerDeathRate", pKeys, labels);
		flowers.addOption(flowerDeath);
		flowers.addMappedMultiOption("FlowerGrowthRate", pKeys, labels);
		flowers.addToggle("FlowersGrow");
		
		cactii.addMappedMultiOption("CactiiDeathRate", pKeys, labels);
		cactii.addOption(cactiiDeath);
		cactii.addMappedMultiOption("CactiiGrowthRate", pKeys, labels);
		cactii.addToggle("CactiiGrow");
		
		reed.addMappedMultiOption("ReedDeathRate", pKeys, labels);
		reed.addOption(reedDeath);
		reed.addMappedMultiOption("ReedGrowthRate", pKeys, labels);
		reed.addToggle("ReedsGrow");
		
		grass.addMappedMultiOption("GrassDeathRate", pKeys, labels);
		grass.addOption(grassDeath);
		grass.addMappedMultiOption("GrassGrowthRate", pKeys, labels);
		grass.addToggle("GrassGrows");
	}
	
	/**
	* Set up misc options
	*/
	private void addMiscOptions() {
		// Mossy cobble growth speed related
		Integer[] pKeys 	= {2400, 240, 30, 5, 30000, 9000};
		// Reproduction rate speed
		Integer[] rKeys 	= { 16000, 1600, 160, 16, 64000, 32000 };
		
		misc.addOption(waterFix);
		misc.addOption(infiniteFire);
		infiniteFire.setGlobalValue(false);
		misc.addMappedMultiOption("MossGrowthRate", pKeys, labels);
		misc.addOption(mossGrows);
		misc.addMappedMultiOption("Animal Birth Rate", rKeys, labels);
		
		
		mossGrowthRate 	 = (ModMappedMultiOption) misc.getOption("MossGrowthRate");
		reproductionRate = (ModMappedMultiOption) misc.getOption("Animal Birth Rate"); 
	}
	
	/**
	* Add custom items
	*/
	private void setupItemsAndBlocks() {
		Item.itemsList[Block.sapling.blockID] 		= (new ItemSapling(Block.sapling.blockID - 256)).setItemName("Sapling");
		Item.itemsList[Block.mushroomBrown.blockID] = (new ItemMushroom(Block.mushroomBrown.blockID - 256)).setItemName("Mushroom");
		Item.itemsList[Block.mushroomRed.blockID] 	= (new ItemMushroom(Block.mushroomRed.blockID - 256)).setItemName("Mushroom");
		Item.itemsList[Block.plantYellow.blockID]	= (new ItemFlower(Block.plantYellow.blockID - 256)).setItemName("Flower");
		Item.itemsList[Block.plantRed.blockID] 		= (new ItemFlower(Block.plantRed.blockID - 256)).setItemName("Flower");
		Item.itemsList[Block.cactus.blockID] 		= (new ItemCactus(Block.cactus.blockID - 256)).setItemName("Cactus");
		
		// Put the cobblestone mossy into the blocklist
		BlockCobblestoneMossy.createInBlockList();
		Block.blocksList[Block.cobblestoneMossy.blockID].initializeBlock();
		
		Item tmp = new ItemBlock(Block.cobblestoneMossy.blockID - 256);;
		Item.itemsList[Block.cobblestoneMossy.blockID] = tmp;
		Item.itemsList[Block.cobblestoneMossy.blockID + 92] = tmp;
		
	}
	
	//=====================
	// ACTION METHODS
	//=====================
	
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
			biome.getBiomeFromString(biomeName);
			biome = biome.getBiomeFromString(biomeName);
			biomeMod = biomeModifier.get(name);
		
			if(biomeMod != null) {
				return biomeMod[biome.getIndex()];
			} else {
				System.out.println("biome Mod Type missing: " + name);
				return 0;
			}
		} catch (NullPointerException e) {
			System.out.println("biome missing: " + biomeName);
			return 0;
		}
	}
	
	/**
	* Sets up the biome modifiers
	*/
	private void createbiomeModifiers() {
		
		//=====
		// NATURE OVERHAUL SPECIFIC biome MODIFIERS
		//=====
						  //  0   1   2    3    4    5    6    7    8  9
		byte[] saplingSpawn = { -10,  0, 10,-100, -95, -100,  0, -20,  10, 0 };
		byte[] saplingDeath = {  10,  0, 10,  95,  95,  100,  0,  20,   5, 0 };
		byte[] treeDeath	= { -10,  0,  0, -90, -90,  100,  0,  20,  10, 0 };
		byte[] bigTree		= {  10, 10, 10,   5,   0,    0,  0,   0,  10, 0 };
		byte[] treeGap		= {   3,  1,  2,  10,   9,    0,  0,   4,   2, 0 };
		byte[] flowerSpawn	= {  25, 10,  0,-100, -90, -100,  0,   0,  50, 0 };
		byte[] cactiSpawn	= { -25,  0,-75,  20, -90, -100,  0,  -5,   0, 0 };
		byte[] cactiiDeath	= {  25,  0, 75, -20,  90,  100,  0, -10,   0, 0 };
		byte[] reedSpawn	= {  50, 10,-90, -90, -10, -100,  0, -40, 100, 0 };
		byte[] reedDeath	= { -50,-20,  5,  10,   0,  100,  0,  40, -20, 0 };
		byte[] shroomSpawn	= {  25, 10,  0, -95, -90, -100,  0,   0,  35, 0 };
		byte[] shroomDeath  = { -25,-10,  0,  95,  90,  100,  0,   0,  10, 0 };
		byte[] mossGrowth 	= { 100, 50,-90,-100, -90, -100,  0,   0, 100, 0 };
		 
		biomeModifier.put("SaplingSpawn", saplingSpawn);
		biomeModifier.put("SaplingDeath", saplingDeath);
		biomeModifier.put("TreeDeath", treeDeath);
		biomeModifier.put("BigTree", bigTree);
		biomeModifier.put("TreeGap", treeGap);
		biomeModifier.put("FlowerSpawn", flowerSpawn);
		biomeModifier.put("CactiiSpawn", cactiSpawn);
		biomeModifier.put("CactiiDeath", cactiiDeath);
		biomeModifier.put("ReedSpawn", reedSpawn);
		biomeModifier.put("ReedDeath", reedDeath);
		biomeModifier.put("ShroomSpawn", shroomSpawn);
		biomeModifier.put("ShroomDeath", shroomDeath);
		biomeModifier.put("GrassSpawn", flowerSpawn);
		biomeModifier.put("MossGrowth", mossGrowth);
		
		//=====
		// STANDARD biome MODIFIERS - Don't edit
		//=====
		byte[] standardDeath = { 25, -10, 45, 90, 60, 100, 50, 65, -25, 100 };
		
		biomeModifier.put("StandardDeath", standardDeath);
	}
	
	/**
	* Add biome modifier
	*
	* @throws	ArrayOutOfBoundsException
	* @param	name	Identifier for this modifier, overwrites old ones if same name.
	* @param	mods	Array of modifiers. Must be 11 long
	*/
	public void addbiomeModifier(String name, byte[] mods) {
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
			// Get biome info
			String name = mod_AutoForest.getBiomeName(i,k);
			
			value = (value + (value * 0.01 * getBiomeModifier(name, biomeMod)));
		} 
		
		return value;
	}
	
	/**
	* Get the biome name
	*
	* @param	i coord
	* @param	j coord
	* @return	Name of biome
	*/
    public static String getBiomeName(int i, int j) {
		WorldChunkManager cm = ModLoader.getMinecraftInstance().theWorld
										.getWorldChunkManager();
        return cm.getBiomeGenAt(i, j).biomeName;
    }
	
	/**
	* The biome at position
	*
	* @param	i coord
	* @param	j coord
	* @return	Biome Enum object
	*/
	public static Biome getBiomeAt(int i, int j) {
		String name = getBiomeName(i, j);
		
		return Biome.getBiomeFromString(name);
	}
	
	//=====================
	// DEBUG METHODS
	//=====================
	
	/**
	* Print a message out specifically for NO mod debugging
	*
	* @param	msg		Message to print
	*/
	public static void debugOutput(String msg) {
		System.out.println("(NatureOverhaul) " + msg);
	}
}
