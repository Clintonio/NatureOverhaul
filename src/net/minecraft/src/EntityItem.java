// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.Random;

//========
// BEGIN NATURE OVERHAUL
//========
import moapi.ModOptionsAPI;
import moapi.ModBooleanOption;
import moapi.ModOptions;
import moapi.ModMappedMultiOption;
//========
// END NATURE OVERHAUL
//========

public class EntityItem extends Entity
{

    public ItemStack item;
    private int field_803_e;
    public int age;
    public int delayBeforeCanPickup;
    private int health;
    public float field_804_d;

	//========
	// BEGIN NATURE OVERHAUL
	//========
    public EntityItem(World world, double d, double d1, double d2, 
            ItemStack itemstack) {
		this(world, d, d1, d2, itemstack, false);
    }
	
	/**
	* Constructor
	*
	* @param	grown	If true, the item will fly further than usual
	*/
    public EntityItem(World world, double d, double d1, double d2, 
            ItemStack itemstack, boolean grown) {
        super(world);
        age = 0;
        health = 5;
        field_804_d = (float)(Math.random() * 3.1415926535897931D * 2D);
        setSize(0.25F, 0.25F);
        yOffset = height / 2.0F;
        setPosition(d, d1, d2);
        item = itemstack;
        rotationYaw = (float)(Math.random() * 360D);
        motionX = (float)(Math.random() * 0.2D - 0.1D);
        motionY = 0.2D;
        motionZ = (float)(Math.random() * 0.2D - 0.1D);
		
		// Set initial velocity if not punched and only grown
		if((!world.multiplayerWorld) && (grown)) {
			setInitialVelocity();
		}
	}
	//======== 
	// END NATURE OVERHAUL
	//========

    protected boolean canTriggerWalking()
    {
        return false;
    }

    public EntityItem(World world)
    {
        super(world);
        age = 0;
        health = 5;
        field_804_d = (float)(Math.random() * 3.1415926535897931D * 2D);
        setSize(0.25F, 0.25F);
        yOffset = height / 2.0F;
    }

    protected void entityInit()
    {
    }

    public void onUpdate()
    {
        super.onUpdate();
        if(delayBeforeCanPickup > 0)
        {
            delayBeforeCanPickup--;
        }
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
		
		//========
		// BEGIN NATURE OVERHAUL
		//========
		if(!ModLoader.getMinecraftInstance().theWorld.multiplayerWorld) {
			setNextYSpeed(item.itemID);
		} else {
			motionY -= 0.04D;
		}
		//========
		// END NATURE OVERHAUL
		//========
		
        if(worldObj.getBlockMaterial(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) == Material.lava)
        {
            motionY = 0.20000000298023221D;
            motionX = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
            motionZ = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
            worldObj.playSoundAtEntity(this, "random.fizz", 0.4F, 2.0F + rand.nextFloat() * 0.4F);
        }
        pushOutOfBlocks(posX, (boundingBox.minY + boundingBox.maxY) / 2D, posZ);
        moveEntity(motionX, motionY, motionZ);
        float f = 0.98F;
        if(onGround)
        {
            f = 0.5880001F;
            int i = worldObj.getBlockId(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ));
            if(i > 0)
            {
                f = Block.blocksList[i].slipperiness * 0.98F;
            }
			
			//========
			// BEGIN NATURE OVERHAUL
			//========
			if(!ModLoader.getMinecraftInstance().theWorld.multiplayerWorld) {
				attemptPlant(i);
			}
			//========
			// END NATURE OVERHAUL
			//========
        }
        motionX *= f;
        motionY *= 0.98000001907348633D;
        motionZ *= f;
        if(onGround)
        {
            motionY *= -0.5D;
        }
        field_803_e++;
        age++;
        if(age >= 6000)
        {
            setEntityDead();
        }
		
		//setEntityDead();
    }
	
	//========
	// BEGIN NATURE OVERHAUL
	//========
	
	/*
	* Set initial speed of items
	*/
	private void setInitialVelocity() {
		double baseSpeed = 0.2D;
		Item item = Item.itemsList[this.item.itemID];
		
		if(item instanceof Plantable) {
			float[] motion = ((Plantable) item).getVelocities(baseSpeed);
			motionX = motion[0];
			motionY = motion[1];
			motionZ = motion[2];
		} else {
			motionX = (float)(Math.random() * baseSpeed - baseSpeed / 2);
			motionY = baseSpeed;
			motionZ = (float)(Math.random() * baseSpeed - baseSpeed / 2);
		}
	}
	
	/**
	* Sets the next Y axis speed based on the item ID
	* Gives a terminal velocity to saplings and plants
	*
	* @param	id	Item id
	*/
	private void setNextYSpeed(int id) {
		Item item = Item.itemsList[id];
		
		if(item instanceof Plantable) {
			if(motionY > (-0.04D * 10)) {
				motionY -= 0.04D;
			}
		} else {
			motionY -= 0.04D;
		}
	}
	
	/**
	* Attempt to plant the current plant item
	*
	* @param	world		World object
	* @param	belowID		ID of block below
	*/
	private void attemptPlant(int belowID) {
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(boundingBox.minY);
		int k = MathHelper.floor_double(posZ);
		// Get block id the entity is occupying
		int curBlockID = worldObj.getBlockId(i, j, k);
		
		// Api-able plantable interface
		if((age > 1200) && ((curBlockID == 0) || (curBlockID == Block.snow.blockID) 
							|| (curBlockID == Block.tallGrass.blockID)
							|| (curBlockID == Block.slowSand.blockID)) 
			&& (Item.itemsList[item.itemID] instanceof Plantable)) {
			Plantable pItem = (Plantable) Item.itemsList[item.itemID];
			if(pItem.plantable(worldObj, i, j, k, belowID, age)) {
				pItem.plant(worldObj, i, j, k, item.getItemDamage());
				setEntityDead();
			}
		}
	}
	
	//========
	// END NATURE OVERHAUL
	//========

    public boolean handleWaterMovement()
    {
        return worldObj.handleMaterialAcceleration(boundingBox, Material.water, this);
    }

    protected void dealFireDamage(int i)
    {
        attackEntityFrom(DamageSource.inFire, i);
    }

    public boolean attackEntityFrom(DamageSource damagesource, int i)
    {
        setBeenAttacked();
        health -= i;
        if(health <= 0)
        {
            setEntityDead();
        }
        return false;
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setShort("Health", (byte)health);
        nbttagcompound.setShort("Age", (short)age);
        nbttagcompound.setCompoundTag("Item", item.writeToNBT(new NBTTagCompound()));
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        health = nbttagcompound.getShort("Health") & 0xff;
        age = nbttagcompound.getShort("Age");
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Item");
        item = ItemStack.loadItemStackFromNBT(nbttagcompound1);
        if(item == null)
        {
            setEntityDead();
        }
    }

    public void onCollideWithPlayer(EntityPlayer entityplayer)
    {
        if(worldObj.multiplayerWorld)
        {
            return;
        }
        int i = item.stackSize;
        if(delayBeforeCanPickup == 0 && entityplayer.inventory.addItemStackToInventory(item))
        {
            if(item.itemID == Block.wood.blockID)
            {
                entityplayer.triggerAchievement(AchievementList.mineWood);
            }
            if(item.itemID == Item.leather.shiftedIndex)
            {
                entityplayer.triggerAchievement(AchievementList.killCow);
            }
            if(item.itemID == Item.diamond.shiftedIndex)
            {
                entityplayer.triggerAchievement(AchievementList.diamonds);
            }
            if(item.itemID == Item.blazeRod.shiftedIndex)
            {
                entityplayer.triggerAchievement(AchievementList.blazeRod);
            }
            ModLoader.OnItemPickup(entityplayer, item);
            worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            entityplayer.onItemPickup(this, i);
            if(item.stackSize <= 0)
            {
                setEntityDead();
            }
        }
    }
}
