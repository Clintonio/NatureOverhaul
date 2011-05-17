package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 


public class ItemDye extends Item
{

    public ItemDye(int i)
    {
        super(i);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public int getIconFromDamage(int i)
    {
        int j = i;
        return iconIndex + (j % 8) * 16 + j / 8;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(super.getItemName()).append(".").append(dyeColors[itemstack.getItemDamage()]).toString();
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
        if(itemstack.getItemDamage() == 15)
        {
			//====================
			// START NATURE OVERHAUL
			//====================
            int i1 = world.getBlockId(i, j, k);
            if(i1 == Block.sapling.blockID) {
                ((BlockSapling)Block.sapling).growTree(world, i, j, k, world.rand);
                itemstack.stackSize--;
                return true;
            } else if(i1 == Block.crops.blockID) {
                ((BlockCrops)Block.crops).fertilize(world, i, j, k);
                itemstack.stackSize--;
                return true;
            } else if(applyBonemeal(world, i, j, k, i1)) {
				itemstack.stackSize--;
				return true;
			}
			//====================
			// END NATURE OVERHAUL
			//====================
        }
        return false;
    }
	
	//====================
	// START NATURE OVERHAUL
	//====================
	/**
	* Apply bonemeal to the item clicked
	* 
	* @param	id 	Item ID
	* @return	true if item is applied
	*/
	private boolean applyBonemeal(World world, int i, int j, int k, int id) {
		// Items affected; cactii, reeds, leaves, flowers and shrooms
		if(Block.blocksList[id] instanceof Growable) {
			((Growable) Block.blocksList[id]).grow(world, i, j, k);
			return true;
		} else {
			return false;
		}
	}
	//====================
	// END NATURE OVERHAUL
	//====================

    public void saddleEntity(ItemStack itemstack, EntityLiving entityliving)
    {
        if(entityliving instanceof EntitySheep)
        {
            EntitySheep entitysheep = (EntitySheep)entityliving;
            int i = BlockCloth.func_21034_c(itemstack.getItemDamage());
            if(!entitysheep.getSheared() && entitysheep.getFleeceColor() != i)
            {
                entitysheep.setFleeceColor(i);
                itemstack.stackSize--;
            }
        }
    }

    public static final String dyeColors[] = {
        "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", 
        "lime", "yellow", "lightBlue", "magenta", "orange", "white"
    };

}
