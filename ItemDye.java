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

    public int func_27009_a(int i)
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
            int i1 = world.getBlockId(i, j, k);
            if(i1 == Block.sapling.blockID)
            {
                ((BlockSapling)Block.sapling).growTree(world, i, j, k, world.rand);
                itemstack.stackSize--;
                return true;
            }
            if(i1 == Block.crops.blockID)
            {
                ((BlockCrops)Block.crops).fertilize(world, i, j, k);
                itemstack.stackSize--;
                return true;
            }
        }
        return false;
    }

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
