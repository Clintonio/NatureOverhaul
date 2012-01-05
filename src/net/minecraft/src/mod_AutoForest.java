package net.minecraft.src;

import moapi.ModOptionsAPI;
import moapi.ModOption;
import moapi.ModBooleanOption;
import moapi.ModOptions;
import moapi.ModMappedOption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

/**
* AutoForest configuration base, sets up options
*
* @author	Clinton Alexander
* @version	0.7
* @since	0.6
*/
public class mod_AutoForest extends BaseMod {
	// Check bottom for static shortcuts
	
	/**
	* The automatic update URL
	*/
	private final String updateURL = "http://minecraft.digitaluppercut.com/updateExists.php";
	/**
	* True if an update exists
	*/
	private boolean updateExists = false;
	
	private static final HashMap<String, byte[]> biomeModifier = new HashMap<String, byte[]>();
	
	/**
	* Version
	*/
	public String getVersion() {
		return "17";
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
		setupModOptions();
		
		if(autoUpdate.getValue()) {
			checkForUpdates(updateURL);
		}
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
		
		options.addOption(autoUpdate);
		options.setWideOption(autoUpdate.getName());
		
		// Sapling related
		setupSaplings();
		// Tree options
		setupTreeOptions();
		// Set up flower options
		addFlowers();
		// Set up shrooms
		setupShroomOptions();
		
		// Climate related
		climate.addOption(biomeModifiedGrowth);
		climate.setWideOption("BiomeModifiedGrowth");
		
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
		Integer[] dKeys 	= {5000, 2500, 250, 5, 20000, 10000};
		
		saps.addMappedOption("DeathRate", dKeys, labels);
		saps.addToggle("SaplingDeath");
		saps.addMultiOption("GrowthRate", labels);
		saps.addToggle("AutoSapling");
		saps.addMappedOption("Growth Occurs On", growthValues, 
								  growthLabels);
		growthType = (ModMappedOption) saps.getOption("Growth Occurs On");
		
		saps.setWideOption("Growth Occurs On");
	}
	
	/**
	* Setup tree options
	*/
	private void setupTreeOptions() {
		Integer[] dKeys 	= {5000, 2500, 250, 5, 20000, 10000};
		Integer[] keys 	= {5, 3, 1, 0, 9, 7};
		String[]  values 	= {"DEFAULT/AVERAGE", "FAST", "VERY FAST", "INSTANT", "VERY SLOW", "SLOW"};
		
		tree.addMappedOption("TreeGrowthRate", keys, values);
		tree.addOption(leafDecay);
		tree.addToggle("Lumberjack");
		tree.addMappedOption("DeathRate", dKeys, labels);
		tree.addToggle("TreeDeath");
		
		// Tree droptions
		Integer[] aKeys = {3000, 1200, 250, 5, 30000, 10000};
		tree.addMappedOption("CocoaGrowthRate", aKeys, labels);
		tree.addToggle("CocoaGrows");
		tree.addMappedOption("AppleGrowthRate", aKeys, labels);
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
		shroomDeathRate  = new ModMappedOption("ShroomDeathRate", pKeys, labels);
		
		shrooms.addOption(defaultShroomSpread);
		shrooms.addOption(shroomTreeGrowth);
		shrooms.addOption(shroomTreesGrow);
		shrooms.addOption(shroomDeathRate);
		shrooms.addOption(shroomDeath);
		shrooms.addMappedOption("ShroomGrowthRate", pKeys, labels);
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
		plants.addSubOptions(wort);
		plants.addSubOptions(cactii);
		plants.addSubOptions(reed);
		plants.addSubOptions(grass);
		
		ModMappedOption drate = new ModMappedOption("FlowerDeathRate", "Death Rate", pKeys, labels);
		ModMappedOption grate = new ModMappedOption("FlowerGrowthRate", "Growth Rate", pKeys, labels);
		
		// Plant submenus
		flowers.addOption(drate);
		flowers.addOption(flowerDeath);
		flowers.addOption(grate);
		flowers.addOption(flowersGrow);
		
		drate = new ModMappedOption("WortDeathRate", "Death Rate", pKeys, labels);
		grate = new ModMappedOption("WortGrowthRate", "Growth Rate", pKeys, labels);
		
		// NetherWort submenus
		wort.addOption(drate);
		wort.addOption(wortDeath);
		wort.addOption(grate);
		wort.addOption(wortsGrow);
		
		drate = new ModMappedOption("CactiiDeathRate", "Death Rate", pKeys, labels);
		grate = new ModMappedOption("CactiiGrowthRate", "Growth Rate", pKeys, labels);
		
		cactii.addOption(drate);
		cactii.addOption(cactiiDeath);
		cactii.addOption(grate);
		cactii.addOption(cactiiGrow);
		
		drate = new ModMappedOption("ReedDeathRate", "Death Rate", pKeys, labels);
		grate = new ModMappedOption("ReedGrowthRate", "Growth Rate", pKeys, labels);
		
		reed.addOption(drate);
		reed.addOption(reedDeath);
		reed.addOption(grate);
		reed.addOption(reedsGrow);
		
		drate = new ModMappedOption("GrassDeathRate", "Death Rate", pKeys, labels);
		grate = new ModMappedOption("GrassGrowthRate", "Growth Rate", pKeys, labels);
		
		grass.addOption(drate);
		grass.addOption(grassDeath);
		grass.addOption(grate);
		grass.addOption(grassGrows);
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
		misc.addMappedOption("MossGrowthRate", pKeys, labels);
		misc.addOption(mossGrows);
		misc.addMappedOption("Animal Birth Rate", rKeys, labels);
		
		
		mossGrowthRate 	 = (ModMappedOption) misc.getOption("MossGrowthRate");
		reproductionRate = (ModMappedOption) misc.getOption("Animal Birth Rate"); 
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
		Item.itemsList[Item.netherStalkSeeds.shiftedIndex]= (new ItemNetherSeeds(116, Block.netherStalk.blockID, Block.slowSand.blockID)).setIconCoord(13, 7).setItemName("netherStalkSeeds").setPotionModifier("+4");
		
		// Put the cobblestone mossy into the blocklist
		BlockCobblestoneMossy.createInBlockList();
		Block.blocksList[Block.cobblestoneMossy.blockID].initializeBlock();
		
		Item tmp = new ItemBlock(Block.cobblestoneMossy.blockID - 256);;
		Item.itemsList[Block.cobblestoneMossy.blockID] = tmp;
		Item.itemsList[Block.cobblestoneMossy.blockID + 92] = tmp;
		
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
	
	//=====================
	// ACTION METHODS
	//=====================
	
	/**
	* Check for an update
	*
	* @param	urlStr 	URL to check for updates
	*/
	private void checkForUpdates(String urlStr) {
		try {
			// Construct data
			String data = URLEncoder.encode("mod", "UTF-8") + "=" + URLEncoder.encode("no", "UTF-8");
			data += "&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode(getVersion(), "UTF-8");

			// Send data
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(1000);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String resp = rd.readLine();
			
			wr.close();
			rd.close();
			System.out.println(resp);
			if(resp.equals("1")) {
				updateExists = true;
				ModLoader.SetInGameHook(this, true, true);
			}
		} catch (Exception e) {
			System.err.println("Could not check for new NatureOverhaul version");
			e.printStackTrace();
		}
	}
	
	/**
	* Update tick ingame, notify user of updates
	*/
	public boolean OnTickInGame(float tick, net.minecraft.client.Minecraft game) {
		if(updateExists) {
			game.ingameGUI.addChatMessage("An update exists for Nature Overhaul");
		}
		
		ModLoader.SetInGameHook(this, false, false);
		
		return true;
	}
	
	//=====================
	// CONVENIENCE OBJECTS
	//=====================
	
	// Constants
	public static final String PLANT_MENU_NAME	= "Plants Options";
	public static final String SAPLING_MENU_NAME = "Sapling Options";
	public static final String MENU_NAME 		= "NatureOverhaul"; 
	public static final String TREE_MENU_NAME	= "Tree Options";
	public static final String CLIMATE_MENU_NAME = "Climate Options";
	public static final String NIGHT_MENU_NAME	= "Day and Night Options";
	public static final String FLOWER_MENU_NAME	= "Flower Options";
	public static final String WORT_MENU_NAME	= "Netherwort Options";
	public static final String CACTI_MENU_NAME	= "Cactus Options";
	public static final String REED_MENU_NAME	= "Reed Options";
	public static final String SHROOMS_MENU_NAME = "Mushroom Options";
	private static final String GRASS_MENU_NAME  = "Grass Options";
	private static final String MISC_MENU_NAME	= "Misc Options";
	
	// General option menus
	
	public static ModOptions options = new ModOptions(MENU_NAME);
	
	// Options for saplings only
	public static ModOptions saps = new ModOptions(SAPLING_MENU_NAME);
	
	// Options for trees
	public static ModOptions tree = new ModOptions(TREE_MENU_NAME);
	public static ModBooleanOption leafDecay = new ModBooleanOption("QuickLeafDecay");
	
	// Options for plants
	public static ModOptions 	plants	= new ModOptions(PLANT_MENU_NAME);
	public static ModOptions 	flowers 	= new ModOptions(FLOWER_MENU_NAME);
	public static ModOptions		wort		= new ModOptions(WORT_MENU_NAME);
	public static ModOptions 	cactii 	= new ModOptions(CACTI_MENU_NAME);
	public static ModOptions 	reed 	= new ModOptions(REED_MENU_NAME);
	public static ModOptions 	shrooms 	= new ModOptions(SHROOMS_MENU_NAME);
	public static ModOptions		grass	= new ModOptions(GRASS_MENU_NAME);
	public static ModOptions		misc		= new ModOptions(MISC_MENU_NAME);
	
	public static ModBooleanOption flowerDeath 	= new ModBooleanOption("Flowers Die", "Can Die");
	public static ModBooleanOption flowersGrow	= new ModBooleanOption("FlowersGrow", "Can Grow");
	
	public static ModBooleanOption reedDeath 	= new ModBooleanOption("Reeds Die", "Can Die");
	public static ModBooleanOption reedsGrow	= new ModBooleanOption("ReedsGrow", "Can Grow");
	
	public static ModBooleanOption cactiiDeath 	= new ModBooleanOption("Cactii Die", "Can Die");
	public static ModBooleanOption cactiiGrow	= new ModBooleanOption("CactiiGrow", "Can Grow");
	
	public static ModBooleanOption grassDeath 	= new ModBooleanOption("Grass Dies", "Can Die");
	public static ModBooleanOption grassGrows	= new ModBooleanOption("GrassGrows", "Can Grow");
	
	// Wort options
	public static ModBooleanOption wortDeath 	= new ModBooleanOption("WortDies", "Can Die");
	public static ModBooleanOption wortsGrow	= new ModBooleanOption("WortGrows", "Can Reproduce");
	
	
	// Misc options
	public static ModBooleanOption biomeModifiedGrowth = new ModBooleanOption("BiomeModifiedGrowth");
	public static ModBooleanOption autoUpdate 		 = new ModBooleanOption("Automatic Update Checks");
	
	// Options for shrooms
	public static ModBooleanOption 	shroomDeath 	= new ModBooleanOption("Shrooms Die", "Can Die");
	public static ModMappedOption 	shroomDeathRate;
	public static ModBooleanOption 	shroomTreesGrow = new ModBooleanOption("ShroomsTreesGrow");
	public static ModMappedOption  	shroomTreeGrowth;
	public static ModBooleanOption	defaultShroomSpread = new ModBooleanOption("Enable Default Spread");
	
	// Options for saplings
	public static ModMappedOption 	growthType;
	
	// Options for misc
	public static ModBooleanOption	mossGrows		= new ModBooleanOption("Moss Grows");
	public static ModMappedOption		mossGrowthRate;
	public static ModBooleanOption	infiniteFire	= new ModBooleanOption("Infinite Fire Spread");
	public static ModBooleanOption 	waterFix		= new ModBooleanOption("Fix Water Bug");
	public static ModMappedOption		reproductionRate;
	
	// Objects
	public static ModOptions climate		= new ModOptions(CLIMATE_MENU_NAME);
	
	// Default labels
	private static String[] labels = {"AVERAGE", "FAST", "SUPERFAST", "INSANE", "SUPERSLOW", "SLOW"};
}
