package net.minecraft.src;

/**
* Sapling Item Representation for varied saplings.
*
* @author	Clinton Alexander
* @version	1.0.0.0
*/
public class ItemSapling extends ItemBlock {
	
    public ItemSapling(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int func_21012_a(int i)
    {
        return i;
    }

    public String getItemNameIS(ItemStack itemstack) {
		int dmg = itemstack.getItemDamage();
		
		if(dmg == 1) {
			return "pinesapling"; 
		} else if(dmg == 2) {
			return "birchsapling";
		} else {
			return "sapling";
		}
    }
}
