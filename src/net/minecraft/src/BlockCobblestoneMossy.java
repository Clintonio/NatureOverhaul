// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.// Jad home page: http://www.kpdus.com/jad.html// Decompiler options: packimports(3) braces deadcode package net.minecraft.src;import java.util.Random;import moapi.ModMappedMultiOption;import moapi.ModBooleanOption;import moapi.ModOptions;/*** Mossy cobblestone** @author	Clinton Alexander*/public class BlockCobblestoneMossy extends BlockGrowable {	protected float optRain = 1.0F;	protected float optTemp = 0.7F;		public final int blockID;	    protected BlockCobblestoneMossy(int i, int j) {        super(i + 92, j, Material.rock);				// Bypass the annoying constructor issue in Block		blocksList[i] = this;		blockID = i;		opaqueCubeLookup[i] = isOpaqueCube();		lightOpacity[i] = isOpaqueCube() ? 255 : 0;		canBlockGrass[i] = !Material.rock.getCanBlockGrass();		isBlockContainer[i] = false;		tickOnLoad[blockID] = true;		        setTickOnLoad(true);    }		/**	* Returns a tickrate	*	* @return	Update every 10 frames	*/    public int tickRate() {        return 30;    }		/**	* Use the cobblestone drop ID	*/    public int idDropped(int i, Random random) {        return blockID;    }		/**	* Update the cobble mossy stats	*/    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {        entityplayer.addStat(StatList.mineBlockStatArray[blockID], 1);        dropBlockAsItem(world, i, j, k, l, 0);    }		/**	* Create the only mossy cobble instance in the block list	*/	public static void createInBlockList() {		int id = Block.cobblestoneMossy.blockID;		if(!(Block.blocksList[id] instanceof BlockCobblestoneMossy)) {			Block.blocksList[id] = (new BlockCobblestoneMossy(48, 36))							   .setHardness(2.0F).setResistance(10F)							   .setStepSound(soundStoneFootstep).setBlockName("stoneMoss");		}	}		// On tick update, attempt to spread to any near cobblestone    public void updateTick(World world, int i, int j, int k, Random random) {		if(!world.multiplayerWorld) {			boolean grow = mod_AutoForest.mossGrows.getValue();			if(grow) {				attemptGrowth(world, i, j, k);			}		}	}		/**	* Get the growth probability	*	* @return	Growth probability	*/	public float getGrowthProb(World world, int i, int j, int k) {		BiomeGenBase biome = BiomeUtil.getBiome(i, k);				float freq = mod_AutoForest.mossGrowthRate.getValue();				if((biome.rainfall < 0.2F) || (biome.temperature > 1F)) {			return 0F;		} else {			freq = freq * getOptValueMult(biome.rainfall, optRain, 10F);			freq = freq * getOptValueMult(biome.temperature, optTemp, 2F);					return 1F / freq;		}	}		/**	* Grow an item	*/	public void grow(World world, int i, int j, int k) {		int scanSize = 1;		int metadata = world.getBlockMetadata(i, j, k);		int id = idDropped(metadata, world.rand);		if((id >= 0) && (id < Item.itemsList.length)) {			for(int x = i - scanSize; x <= i + scanSize; x++) {				for(int y = j - scanSize; y <= j + scanSize; y++) {					for(int z = k - scanSize; z <= k + scanSize; z++) {						if(world.getBlockId(x, y, z) == Block.cobblestone.blockID) {							world.setBlockWithNotify(x, y, z, blockID);							return;						}					}				}			}		}	}}