package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;

public class EntityItem extends Entity
{

    public EntityItem(World world, double d, double d1, double d2, 
            ItemStack itemstack)
    {
        super(world);
        age = 0;
        health = 5;
        field_804_d = (float)(Math.random() * 3.1415926535897931D * 2D);
        setSize(0.25F, 0.25F);
        yOffset = height / 2.0F;
        setPosition(d, d1, d2);
        item = itemstack;
        rotationYaw = (float)(Math.random() * 360D);
        motionX = (float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D);
        motionY = 0.20000000298023224D;
        motionZ = (float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D);
    }

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
        motionY -= 0.039999999105930328D;
        if(worldObj.getBlockMaterial(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) == Material.lava)
        {
            motionY = 0.20000000298023224D;
            motionX = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
            motionZ = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
            worldObj.playSoundAtEntity(this, "random.fizz", 0.4F, 2.0F + rand.nextFloat() * 0.4F);
        }
        func_466_g(posX, posY, posZ);
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
    }

    public boolean handleWaterMovement()
    {
        return worldObj.handleMaterialAcceleration(boundingBox, Material.water, this);
    }

    private boolean func_466_g(double d, double d1, double d2)
    {
        int i = MathHelper.floor_double(d);
        int j = MathHelper.floor_double(d1);
        int k = MathHelper.floor_double(d2);
        double d3 = d - (double)i;
        double d4 = d1 - (double)j;
        double d5 = d2 - (double)k;
        if(Block.opaqueCubeLookup[worldObj.getBlockId(i, j, k)])
        {
            boolean flag = !Block.opaqueCubeLookup[worldObj.getBlockId(i - 1, j, k)];
            boolean flag1 = !Block.opaqueCubeLookup[worldObj.getBlockId(i + 1, j, k)];
            boolean flag2 = !Block.opaqueCubeLookup[worldObj.getBlockId(i, j - 1, k)];
            boolean flag3 = !Block.opaqueCubeLookup[worldObj.getBlockId(i, j + 1, k)];
            boolean flag4 = !Block.opaqueCubeLookup[worldObj.getBlockId(i, j, k - 1)];
            boolean flag5 = !Block.opaqueCubeLookup[worldObj.getBlockId(i, j, k + 1)];
            byte byte0 = -1;
            double d6 = 9999D;
            if(flag && d3 < d6)
            {
                d6 = d3;
                byte0 = 0;
            }
            if(flag1 && 1.0D - d3 < d6)
            {
                d6 = 1.0D - d3;
                byte0 = 1;
            }
            if(flag2 && d4 < d6)
            {
                d6 = d4;
                byte0 = 2;
            }
            if(flag3 && 1.0D - d4 < d6)
            {
                d6 = 1.0D - d4;
                byte0 = 3;
            }
            if(flag4 && d5 < d6)
            {
                d6 = d5;
                byte0 = 4;
            }
            if(flag5 && 1.0D - d5 < d6)
            {
                double d7 = 1.0D - d5;
                byte0 = 5;
            }
            float f = rand.nextFloat() * 0.2F + 0.1F;
            if(byte0 == 0)
            {
                motionX = -f;
            }
            if(byte0 == 1)
            {
                motionX = f;
            }
            if(byte0 == 2)
            {
                motionY = -f;
            }
            if(byte0 == 3)
            {
                motionY = f;
            }
            if(byte0 == 4)
            {
                motionZ = -f;
            }
            if(byte0 == 5)
            {
                motionZ = f;
            }
        }
        return false;
    }

    protected void dealFireDamage(int i)
    {
        attackEntityFrom(null, i);
    }

    public boolean attackEntityFrom(Entity entity, int i)
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
        item = new ItemStack(nbttagcompound1);
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
            worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            entityplayer.onItemPickup(this, i);
            setEntityDead();
        }
    }

    public ItemStack item;
    private int field_803_e;
    public int age;
    public int delayBeforeCanPickup;
    private int health;
    public float field_804_d;
}
