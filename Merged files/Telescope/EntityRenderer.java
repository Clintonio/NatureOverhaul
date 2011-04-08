package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;

public class EntityRenderer
{

    public EntityRenderer(Minecraft minecraft)
    {
        farPlaneDistance = 0.0F;
        field_1385_k = null;
        mouseFilterXAxis = new MouseFilter();
        mouseFilterYAxis = new MouseFilter();
        field_22233_n = new MouseFilter();
        field_22232_o = new MouseFilter();
        field_22231_p = new MouseFilter();
        field_22229_q = new MouseFilter();
        field_22228_r = 4F;
        field_22227_s = 4F;
        field_22226_t = 0.0F;
        field_22225_u = 0.0F;
        field_22224_v = 0.0F;
        field_22223_w = 0.0F;
        field_22222_x = 0.0F;
        field_22221_y = 0.0F;
        field_22220_z = 0.0F;
        field_22230_A = 0.0F;
        cameraZoom = 1.0D;
        cameraYaw = 0.0D;
        cameraPitch = 0.0D;
        prevFrameTime = System.currentTimeMillis();
        random = new Random();
        field_1394_b = 0;
        field_1393_c = 0;
        field_1392_d = GLAllocation.createDirectFloatBuffer(16);
        mc = minecraft;
        itemRenderer = new ItemRenderer(minecraft);
    }

    public void updateRenderer()
    {
        field_1382_n = field_1381_o;
        field_22227_s = field_22228_r;
        field_22225_u = field_22226_t;
        field_22223_w = field_22224_v;
        field_22221_y = field_22222_x;
        field_22230_A = field_22220_z;
        if(mc.renderViewEntity == null)
        {
            mc.renderViewEntity = mc.thePlayer;
        }
        float f = mc.theWorld.getLightBrightness(MathHelper.floor_double(mc.renderViewEntity.posX), MathHelper.floor_double(mc.renderViewEntity.posY), MathHelper.floor_double(mc.renderViewEntity.posZ));
        float f1 = (float)(3 - mc.gameSettings.renderDistance) / 3F;
        float f2 = f * (1.0F - f1) + f1;
        field_1381_o += (f2 - field_1381_o) * 0.1F;
        field_1386_j++;
        itemRenderer.updateEquippedItem();
        if(mc.isRaining)
        {
            addRainParticles();
        }
    }

    public void getMouseOver(float f)
    {
        if(mc.renderViewEntity == null)
        {
            return;
        }
        if(mc.theWorld == null)
        {
            return;
        }
        double d = mc.playerController.getBlockReachDistance();
        mc.objectMouseOver = mc.renderViewEntity.rayTrace(d, f);
        double d1 = d;
        Vec3D vec3d = mc.renderViewEntity.getPosition(f);
        if(mc.objectMouseOver != null)
        {
            d1 = mc.objectMouseOver.hitVec.distanceTo(vec3d);
        }
        if(mc.playerController instanceof PlayerControllerTest)
        {
            d1 = d = 32D;
        } else
        {
            if(d1 > 3D)
            {
                d1 = 3D;
            }
            d = d1;
        }
        Vec3D vec3d1 = mc.renderViewEntity.getLook(f);
        Vec3D vec3d2 = vec3d.addVector(vec3d1.xCoord * d, vec3d1.yCoord * d, vec3d1.zCoord * d);
        field_1385_k = null;
        float f1 = 1.0F;
        List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(vec3d1.xCoord * d, vec3d1.yCoord * d, vec3d1.zCoord * d).expand(f1, f1, f1));
        double d2 = 0.0D;
        for(int i = 0; i < list.size(); i++)
        {
            Entity entity = (Entity)list.get(i);
            if(!entity.canBeCollidedWith())
            {
                continue;
            }
            float f2 = entity.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
            MovingObjectPosition movingobjectposition = axisalignedbb.func_1169_a(vec3d, vec3d2);
            if(axisalignedbb.isVecInside(vec3d))
            {
                if(0.0D < d2 || d2 == 0.0D)
                {
                    field_1385_k = entity;
                    d2 = 0.0D;
                }
                continue;
            }
            if(movingobjectposition == null)
            {
                continue;
            }
            double d3 = vec3d.distanceTo(movingobjectposition.hitVec);
            if(d3 < d2 || d2 == 0.0D)
            {
                field_1385_k = entity;
                d2 = d3;
            }
        }

        if(field_1385_k != null && !(mc.playerController instanceof PlayerControllerTest))
        {
            mc.objectMouseOver = new MovingObjectPosition(field_1385_k);
        }
    }

    private float func_914_d(float f)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        float f1 = 70F;
        if(entityliving.isInsideOfMaterial(Material.water))
        {
            f1 = 60F;
        }
        if(entityliving.health <= 0)
        {
            float f2 = (float)entityliving.deathTime + f;
            f1 /= (1.0F - 500F / (f2 + 500F)) * 2.0F + 1.0F;
        }
        return f1 + field_22221_y + (field_22222_x - field_22221_y) * f;
    }

    private void hurtCameraEffect(float f)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        float f1 = (float)entityliving.hurtTime - f;
        if(entityliving.health <= 0)
        {
            float f2 = (float)entityliving.deathTime + f;
            GL11.glRotatef(40F - 8000F / (f2 + 200F), 0.0F, 0.0F, 1.0F);
        }
        if(f1 < 0.0F)
        {
            return;
        } else
        {
            f1 /= entityliving.maxHurtTime;
            f1 = MathHelper.sin(f1 * f1 * f1 * f1 * 3.141593F);
            float f3 = entityliving.attackedAtYaw;
            GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f1 * 14F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
            return;
        }
    }

    private void setupViewBobbing(float f)
    {
        if(!(mc.renderViewEntity instanceof EntityPlayer))
        {
            return;
        } else
        {
            EntityPlayer entityplayer = (EntityPlayer)mc.renderViewEntity;
            float f1 = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            float f2 = -(entityplayer.distanceWalkedModified + f1 * f);
            float f3 = entityplayer.field_775_e + (entityplayer.field_774_f - entityplayer.field_775_e) * f;
            float f4 = entityplayer.cameraPitch + (entityplayer.field_9328_R - entityplayer.cameraPitch) * f;
            GL11.glTranslatef(MathHelper.sin(f2 * 3.141593F) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * 3.141593F) * f3), 0.0F);
            GL11.glRotatef(MathHelper.sin(f2 * 3.141593F) * f3 * 3F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(Math.abs(MathHelper.cos(f2 * 3.141593F - 0.2F) * f3) * 5F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
            return;
        }
    }

    private void orientCamera(float f)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        float f1 = entityliving.yOffset - 1.62F;
        double d = entityliving.prevPosX + (entityliving.posX - entityliving.prevPosX) * (double)f;
        double d1 = (entityliving.prevPosY + (entityliving.posY - entityliving.prevPosY) * (double)f) - (double)f1;
        double d2 = entityliving.prevPosZ + (entityliving.posZ - entityliving.prevPosZ) * (double)f;
        GL11.glRotatef(field_22230_A + (field_22220_z - field_22230_A) * f, 0.0F, 0.0F, 1.0F);
        if(entityliving.isPlayerSleeping())
        {
            f1 = (float)((double)f1 + 1.0D);
            GL11.glTranslatef(0.0F, 0.3F, 0.0F);
            if(!mc.gameSettings.field_22273_E)
            {
                int i = mc.theWorld.getBlockId(MathHelper.floor_double(entityliving.posX), MathHelper.floor_double(entityliving.posY), MathHelper.floor_double(entityliving.posZ));
                if(i == Block.blockBed.blockID)
                {
                    int j = mc.theWorld.getBlockMetadata(MathHelper.floor_double(entityliving.posX), MathHelper.floor_double(entityliving.posY), MathHelper.floor_double(entityliving.posZ));
                    int k = j & 3;
                    GL11.glRotatef(k * 90, 0.0F, 1.0F, 0.0F);
                }
                GL11.glRotatef(entityliving.prevRotationYaw + (entityliving.rotationYaw - entityliving.prevRotationYaw) * f + 180F, 0.0F, -1F, 0.0F);
                GL11.glRotatef(entityliving.prevRotationPitch + (entityliving.rotationPitch - entityliving.prevRotationPitch) * f, -1F, 0.0F, 0.0F);
            }
        } else
        if(mc.gameSettings.thirdPersonView)
        {
            double d3 = field_22227_s + (field_22228_r - field_22227_s) * f;
            if(mc.gameSettings.field_22273_E)
            {
                float f2 = field_22225_u + (field_22226_t - field_22225_u) * f;
                float f4 = field_22223_w + (field_22224_v - field_22223_w) * f;
                GL11.glTranslatef(0.0F, 0.0F, (float)(-d3));
                GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
            } else
            {
                float f3 = entityliving.rotationYaw;
                float f5 = entityliving.rotationPitch;
                double d4 = (double)(-MathHelper.sin((f3 / 180F) * 3.141593F) * MathHelper.cos((f5 / 180F) * 3.141593F)) * d3;
                double d5 = (double)(MathHelper.cos((f3 / 180F) * 3.141593F) * MathHelper.cos((f5 / 180F) * 3.141593F)) * d3;
                double d6 = (double)(-MathHelper.sin((f5 / 180F) * 3.141593F)) * d3;
                for(int l = 0; l < 8; l++)
                {
                    float f6 = (l & 1) * 2 - 1;
                    float f7 = (l >> 1 & 1) * 2 - 1;
                    float f8 = (l >> 2 & 1) * 2 - 1;
                    f6 *= 0.1F;
                    f7 *= 0.1F;
                    f8 *= 0.1F;
                    MovingObjectPosition movingobjectposition = mc.theWorld.rayTraceBlocks(Vec3D.createVector(d + (double)f6, d1 + (double)f7, d2 + (double)f8), Vec3D.createVector((d - d4) + (double)f6 + (double)f8, (d1 - d6) + (double)f7, (d2 - d5) + (double)f8));
                    if(movingobjectposition == null)
                    {
                        continue;
                    }
                    double d7 = movingobjectposition.hitVec.distanceTo(Vec3D.createVector(d, d1, d2));
                    if(d7 < d3)
                    {
                        d3 = d7;
                    }
                }

                GL11.glRotatef(entityliving.rotationPitch - f5, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(entityliving.rotationYaw - f3, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.0F, (float)(-d3));
                GL11.glRotatef(f3 - entityliving.rotationYaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(f5 - entityliving.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        } else
        {
            GL11.glTranslatef(0.0F, 0.0F, -0.1F);
        }
        if(!mc.gameSettings.field_22273_E)
        {
            GL11.glRotatef(entityliving.prevRotationPitch + (entityliving.rotationPitch - entityliving.prevRotationPitch) * f, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(entityliving.prevRotationYaw + (entityliving.rotationYaw - entityliving.prevRotationYaw) * f + 180F, 0.0F, 1.0F, 0.0F);
        }
        GL11.glTranslatef(0.0F, f1, 0.0F);
    }

    public void func_21152_a(double d, double d1, double d2)
    {
        cameraZoom = d;
        cameraYaw = d1;
        cameraPitch = d2;
    }

    public void resetZoom()
    {
        cameraZoom = 1.0D;
    }

    // MERGE BEGIN
	private boolean kSmooth;
    // MERGE END
	
    private void setupCameraTransform(float f, int i)
    {
        farPlaneDistance = 256 >> mc.gameSettings.renderDistance;
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        float f1 = 0.07F;
        if(mc.gameSettings.anaglyph)
        {
            GL11.glTranslatef((float)(-(i * 2 - 1)) * f1, 0.0F, 0.0F);
        }
		
		
			
     // TELESCOPE BEGIN
        
        float FOV = func_914_d(f);
        
        EntityPlayer entityliving = (EntityPlayer) mc.thePlayer;
        
		if(kSmooth == true)
		{
			mc.gameSettings.smoothCamera = false;
			kSmooth = false;
		}
		
        if(entityliving != null)
        {
        	if(entityliving.inventory.getCurrentItem() != null)
        	{
        		if(entityliving.inventory.getCurrentItem().itemID == mod_Telescope.itemTelescope.shiftedIndex)
		        {
		            ItemTelescope tele = (ItemTelescope) entityliving.inventory.getCurrentItem().getItem();
		            if(tele.getZoom() > 0)
		            {
		            	FOV = FOV / (2.5F * tele.getZoom());
						mc.gameSettings.smoothCamera = true;
						kSmooth = true;
		            }
		        }
        	}
        }
        
        // TELESCOPE END
		
        if(cameraZoom != 1.0D)
        {
            GL11.glTranslatef((float)cameraYaw, (float)(-cameraPitch), 0.0F);
            GL11.glScaled(cameraZoom, cameraZoom, 1.0D);
            GLU.gluPerspective(FOV, (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, farPlaneDistance);
        } else
        {
            GLU.gluPerspective(FOV, (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, farPlaneDistance);
        }
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        if(mc.gameSettings.anaglyph)
        {
            GL11.glTranslatef((float)(i * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }
        hurtCameraEffect(f);
        if(mc.gameSettings.viewBobbing)
        {
            setupViewBobbing(f);
        }
        float f2 = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * f;
        if(f2 > 0.0F)
        {
            float f3 = 5F / (f2 * f2 + 5F) - f2 * 0.04F;
            f3 *= f3;
            GL11.glRotatef(f2 * f2 * 1500F, 0.0F, 1.0F, 1.0F);
            GL11.glScalef(1.0F / f3, 1.0F, 1.0F);
            GL11.glRotatef(-f2 * f2 * 1500F, 0.0F, 1.0F, 1.0F);
        }
        orientCamera(f);
    }

    private void func_4135_b(float f, int i)
    {
        GL11.glLoadIdentity();
        if(mc.gameSettings.anaglyph)
        {
            GL11.glTranslatef((float)(i * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }
        GL11.glPushMatrix();
        hurtCameraEffect(f);
        if(mc.gameSettings.viewBobbing)
        {
            setupViewBobbing(f);
        }
        if(!mc.gameSettings.thirdPersonView && !mc.renderViewEntity.isPlayerSleeping() && !mc.gameSettings.hideGUI)
        {
            itemRenderer.renderItemInFirstPerson(f);
        }
        GL11.glPopMatrix();
        if(!mc.gameSettings.thirdPersonView && !mc.renderViewEntity.isPlayerSleeping())
        {
            itemRenderer.renderOverlays(f);
            hurtCameraEffect(f);
        }
        if(mc.gameSettings.viewBobbing)
        {
            setupViewBobbing(f);
        }
    }

    public void updateCameraAndRender(float f)
    {
        if(!Display.isActive())
        {
            if(System.currentTimeMillis() - prevFrameTime > 500L)
            {
                mc.displayInGameMenu();
            }
        } else
        {
            prevFrameTime = System.currentTimeMillis();
        }
        if(mc.inGameHasFocus)
        {
            mc.mouseHelper.mouseXYChange();
            float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f2 = f1 * f1 * f1 * 8F;
            float f3 = (float)mc.mouseHelper.deltaX * f2;
            float f4 = (float)mc.mouseHelper.deltaY * f2;
            int l = 1;
            if(mc.gameSettings.invertMouse)
            {
                l = -1;
            }
            if(mc.gameSettings.smoothCamera)
            {
                f3 = mouseFilterXAxis.func_22386_a(f3, 0.05F * f2);
                f4 = mouseFilterYAxis.func_22386_a(f4, 0.05F * f2);
            }
            mc.thePlayer.func_346_d(f3, f4 * (float)l);
        }
        if(mc.field_6307_v)
        {
            return;
        }
        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        int k = (Mouse.getX() * i) / mc.displayWidth;
        int i1 = j - (Mouse.getY() * j) / mc.displayHeight - 1;
        if(mc.theWorld != null)
        {
            renderWorld(f);
            if(!mc.gameSettings.hideGUI || mc.currentScreen != null)
            {
                mc.ingameGUI.renderGameOverlay(f, mc.currentScreen != null, k, i1);
            }
        } else
        {
            GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
            GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
            GL11.glLoadIdentity();
            func_905_b();
        }
        if(mc.currentScreen != null)
        {
            GL11.glClear(256);
            mc.currentScreen.drawScreen(k, i1, f);
            if(mc.currentScreen != null && mc.currentScreen.field_25091_h != null)
            {
                mc.currentScreen.field_25091_h.func_25087_a(f);
            }
        }
    }

    public void renderWorld(float f)
    {
        if(mc.renderViewEntity == null)
        {
            mc.renderViewEntity = mc.thePlayer;
        }
        getMouseOver(f);
        EntityLiving entityliving = mc.renderViewEntity;
        RenderGlobal renderglobal = mc.renderGlobal;
        EffectRenderer effectrenderer = mc.effectRenderer;
        double d = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * (double)f;
        double d1 = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * (double)f;
        double d2 = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * (double)f;
        IChunkProvider ichunkprovider = mc.theWorld.getIChunkProvider();
        if(ichunkprovider instanceof ChunkProviderLoadOrGenerate)
        {
            ChunkProviderLoadOrGenerate chunkproviderloadorgenerate = (ChunkProviderLoadOrGenerate)ichunkprovider;
            int j = MathHelper.floor_float((int)d) >> 4;
            int k = MathHelper.floor_float((int)d2) >> 4;
            chunkproviderloadorgenerate.setCurrentChunkOver(j, k);
        }
        for(int i = 0; i < 2; i++)
        {
            if(mc.gameSettings.anaglyph)
            {
                if(i == 0)
                {
                    GL11.glColorMask(false, true, true, false);
                } else
                {
                    GL11.glColorMask(true, false, false, false);
                }
            }
            GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
            updateFogColor(f);
            GL11.glClear(16640);
            GL11.glEnable(2884 /*GL_CULL_FACE*/);
            setupCameraTransform(f, i);
            ClippingHelperImplementation.getInstance();
            if(mc.gameSettings.renderDistance < 2)
            {
                setupFog(-1);
                renderglobal.renderSky(f);
            }
            GL11.glEnable(2912 /*GL_FOG*/);
            setupFog(1);
            if(mc.gameSettings.ambientOcclusion)
            {
                GL11.glShadeModel(7425 /*GL_SMOOTH*/);
            }
            Frustrum frustrum = new Frustrum();
            frustrum.setPosition(d, d1, d2);
            mc.renderGlobal.clipRenderersByFrustrum(frustrum, f);
            mc.renderGlobal.updateRenderers(entityliving, false);
            setupFog(0);
            GL11.glEnable(2912 /*GL_FOG*/);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
            RenderHelper.disableStandardItemLighting();
            renderglobal.sortAndRender(entityliving, 0, f);
            GL11.glShadeModel(7424 /*GL_FLAT*/);
            RenderHelper.enableStandardItemLighting();
            renderglobal.renderEntities(entityliving.getPosition(f), frustrum, f);
            effectrenderer.func_1187_b(entityliving, f);
            RenderHelper.disableStandardItemLighting();
            setupFog(0);
            effectrenderer.renderParticles(entityliving, f);
            if(mc.objectMouseOver != null && entityliving.isInsideOfMaterial(Material.water) && (entityliving instanceof EntityPlayer))
            {
                EntityPlayer entityplayer = (EntityPlayer)entityliving;
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                renderglobal.func_959_a(entityplayer, mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                renderglobal.drawSelectionBox(entityplayer, mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            }
            GL11.glBlendFunc(770, 771);
            setupFog(0);
            GL11.glEnable(3042 /*GL_BLEND*/);
            GL11.glDisable(2884 /*GL_CULL_FACE*/);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
            if(mc.gameSettings.fancyGraphics)
            {
                GL11.glColorMask(false, false, false, false);
                int l = renderglobal.sortAndRender(entityliving, 1, f);
                GL11.glColorMask(true, true, true, true);
                if(mc.gameSettings.anaglyph)
                {
                    if(i == 0)
                    {
                        GL11.glColorMask(false, true, true, false);
                    } else
                    {
                        GL11.glColorMask(true, false, false, false);
                    }
                }
                if(l > 0)
                {
                    renderglobal.func_944_a(1, f);
                }
            } else
            {
                renderglobal.sortAndRender(entityliving, 1, f);
            }
            GL11.glDepthMask(true);
            GL11.glEnable(2884 /*GL_CULL_FACE*/);
            GL11.glDisable(3042 /*GL_BLEND*/);
            
            // Snow Rendering (only if in real dimension)
            if (mc.thePlayer.dimension == 0 && (mod_Snow.SnowMode.getValue() == 2))
            {
                renderSnowMode2(f);
            }
            
            if (mc.thePlayer.dimension == 0 && (mod_Snow.SnowMode.getValue() == 1)) // && (theWorld.biome.equals("Taiga") || theWorld.biome.equals("Ice Desert") || theWorld.biome.equals("Tundra"))))
            {
                renderSnowMode1(f);
            }
            
            if(cameraZoom == 1.0D && (entityliving instanceof EntityPlayer) && mc.objectMouseOver != null && !entityliving.isInsideOfMaterial(Material.water))
            {
                EntityPlayer entityplayer1 = (EntityPlayer)entityliving;
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                renderglobal.func_959_a(entityplayer1, mc.objectMouseOver, 0, entityplayer1.inventory.getCurrentItem(), f);
                renderglobal.drawSelectionBox(entityplayer1, mc.objectMouseOver, 0, entityplayer1.inventory.getCurrentItem(), f);
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            }
            GL11.glDisable(2912 /*GL_FOG*/);
            if(field_1385_k == null);
            setupFog(0);
            GL11.glEnable(2912 /*GL_FOG*/);
            renderglobal.renderClouds(f);
            GL11.glDisable(2912 /*GL_FOG*/);
            setupFog(1);
            if(cameraZoom == 1.0D)
            {
                GL11.glClear(256);
                func_4135_b(f, i);
            }
            if(!mc.gameSettings.anaglyph)
            {
                return;
            }
        }

        GL11.glColorMask(true, true, true, false);
    }

    private void addRainParticles()
    {
        if(!mc.gameSettings.fancyGraphics)
        {
            return;
        }
        EntityLiving entityliving = mc.renderViewEntity;
        World world = mc.theWorld;
        int i = MathHelper.floor_double(entityliving.posX);
        int j = MathHelper.floor_double(entityliving.posY);
        int k = MathHelper.floor_double(entityliving.posZ);
        byte byte0 = 16;
        for(int l = 0; l < 150; l++)
        {
            int i1 = (i + random.nextInt(byte0)) - random.nextInt(byte0);
            int j1 = (k + random.nextInt(byte0)) - random.nextInt(byte0);
            int k1 = world.func_696_e(i1, j1);
            int l1 = world.getBlockId(i1, k1 - 1, j1);
            if(k1 > j + byte0 || k1 < j - byte0)
            {
                continue;
            }
            float f = random.nextFloat();
            float f1 = random.nextFloat();
            if(l1 > 0)
            {
                mc.effectRenderer.addEffect(new EntityRainFX(world, (float)i1 + f, (double)((float)k1 + 0.1F) - Block.blocksList[l1].minY, (float)j1 + f1));
            }
        }

    }

    public void func_905_b()
    {
        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        GL11.glClear(256);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, scaledresolution.field_25121_a, scaledresolution.field_25120_b, 0.0D, 1000D, 3000D);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000F);
    }

    private void updateFogColor(float f)
    {
        World world = mc.theWorld;
        EntityLiving entityliving = mc.renderViewEntity;
        float f1 = 1.0F / (float)(4 - mc.gameSettings.renderDistance);
        f1 = 1.0F - (float)Math.pow(f1, 0.25D);
        Vec3D vec3d = world.func_4079_a(mc.renderViewEntity, f);
        float f2 = (float)vec3d.xCoord;
        float f3 = (float)vec3d.yCoord;
        float f4 = (float)vec3d.zCoord;
        Vec3D vec3d1 = world.getFogColor(f);
        fogColorRed = (float)vec3d1.xCoord;
        fogColorGreen = (float)vec3d1.yCoord;
        fogColorBlue = (float)vec3d1.zCoord;
        fogColorRed += (f2 - fogColorRed) * f1;
        fogColorGreen += (f3 - fogColorGreen) * f1;
        fogColorBlue += (f4 - fogColorBlue) * f1;
        if(entityliving.isInsideOfMaterial(Material.water))
        {
            fogColorRed = 0.02F;
            fogColorGreen = 0.02F;
            fogColorBlue = 0.2F;
        } else
        if(entityliving.isInsideOfMaterial(Material.lava))
        {
            fogColorRed = 0.6F;
            fogColorGreen = 0.1F;
            fogColorBlue = 0.0F;
        }
        float f5 = field_1382_n + (field_1381_o - field_1382_n) * f;
        fogColorRed *= f5;
        fogColorGreen *= f5;
        fogColorBlue *= f5;
        if(mc.gameSettings.anaglyph)
        {
            float f6 = (fogColorRed * 30F + fogColorGreen * 59F + fogColorBlue * 11F) / 100F;
            float f7 = (fogColorRed * 30F + fogColorGreen * 70F) / 100F;
            float f8 = (fogColorRed * 30F + fogColorBlue * 70F) / 100F;
            fogColorRed = f6;
            fogColorGreen = f7;
            fogColorBlue = f8;
        }
        GL11.glClearColor(fogColorRed, fogColorGreen, fogColorBlue, 0.0F);
    }

    private void setupFog(int i)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        GL11.glFog(2918 /*GL_FOG_COLOR*/, func_908_a(fogColorRed, fogColorGreen, fogColorBlue, 1.0F));
        GL11.glNormal3f(0.0F, -1F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(entityliving.isInsideOfMaterial(Material.water))
        {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 2048 /*GL_EXP*/);
            GL11.glFogf(2914 /*GL_FOG_DENSITY*/, 0.1F);
            float f = 0.4F;
            float f2 = 0.4F;
            float f4 = 0.9F;
            if(mc.gameSettings.anaglyph)
            {
                float f6 = (f * 30F + f2 * 59F + f4 * 11F) / 100F;
                float f8 = (f * 30F + f2 * 70F) / 100F;
                float f10 = (f * 30F + f4 * 70F) / 100F;
                f = f6;
                f2 = f8;
                f4 = f10;
            }
        } else
        if(entityliving.isInsideOfMaterial(Material.lava))
        {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 2048 /*GL_EXP*/);
            GL11.glFogf(2914 /*GL_FOG_DENSITY*/, 2.0F);
            float f1 = 0.4F;
            float f3 = 0.3F;
            float f5 = 0.3F;
            if(mc.gameSettings.anaglyph)
            {
                float f7 = (f1 * 30F + f3 * 59F + f5 * 11F) / 100F;
                float f9 = (f1 * 30F + f3 * 70F) / 100F;
                float f11 = (f1 * 30F + f5 * 70F) / 100F;
                f1 = f7;
                f3 = f9;
                f5 = f11;
            }
        } else
        {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 9729 /*GL_LINEAR*/);
            GL11.glFogf(2915 /*GL_FOG_START*/, farPlaneDistance * 0.25F);
            GL11.glFogf(2916 /*GL_FOG_END*/, farPlaneDistance);
            if(i < 0)
            {
                GL11.glFogf(2915 /*GL_FOG_START*/, 0.0F);
                GL11.glFogf(2916 /*GL_FOG_END*/, farPlaneDistance * 0.8F);
            }
            if(GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }
            if(mc.theWorld.worldProvider.field_4220_c)
            {
                GL11.glFogf(2915 /*GL_FOG_START*/, 0.0F);
            }
        }
        GL11.glEnable(2903 /*GL_COLOR_MATERIAL*/);
        GL11.glColorMaterial(1028 /*GL_FRONT*/, 4608 /*GL_AMBIENT*/);
    }

    private FloatBuffer func_908_a(float f, float f1, float f2, float f3)
    {
        field_1392_d.clear();
        field_1392_d.put(f).put(f1).put(f2).put(f3);
        field_1392_d.flip();
        return field_1392_d;
    }
    
    //Snow Mod Rendering
    
    public void renderSnowMode1(float f)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        World world = mc.theWorld;
        int i = MathHelper.floor_double(entityliving.posX);
        int j = MathHelper.floor_double(entityliving.posY);
        int k = MathHelper.floor_double(entityliving.posZ);
        
        String biomeName = world.getBiomeName(i, k);
    	if (biomeName.equals("Taiga") || biomeName.equals("Ice Desert") || biomeName.equals("Tundra"))
    	{
        
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(2884 /*GL_CULL_FACE*/);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/environment/snow.png"));
        double d = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * (double)f;
        double d1 = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * (double)f;
        double d2 = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * (double)f;
        int l = 5;
        if(mc.gameSettings.fancyGraphics)
        {
            l = 10;
        }
        boolean flag = false;
        for(int i1 = i - l; i1 <= i + l; i1++)
        {
            for(int j1 = k - l; j1 <= k + l; j1++)
            {
                int k1 = world.findTopSolidBlock(i1, j1);
                if(k1 < 0)
                {
                    k1 = 0;
                }
                
                int l1 = j - l;
                int i2 = j + l;
                if(l1 < k1)
                {
                    l1 = k1;
                }
                if(i2 < k1)
                {
                    i2 = k1;
                }
                float f1 = 2.0F;
                if(l1 != i2)
                {
                    random.setSeed(i1 * i1 * 3121 /*GL_RGBA_MODE*/ + i1 * 0x2b24abb + j1 * j1 * 0x66397 + j1 * 13761);
                    float f2 = (float)field_1386_j + f;
                    float f3 = ((float)(field_1386_j & 0x1ff) + f) / 512F;
                    float f4 = random.nextFloat() + f2 * 0.01F * (float)random.nextGaussian();
                    float f5 = random.nextFloat() + f2 * (float)random.nextGaussian() * 0.001F;
                    double d3 = (double)((float)i1 + 0.5F) - entityliving.posX;
                    double d4 = (double)((float)j1 + 0.5F) - entityliving.posZ;
                    float f6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4) / (float)l;
                    tessellator.startDrawingQuads();
                    float f7 = world.getLightBrightness(i1, 128, j1);
                    GL11.glColor4f(f7, f7, f7, (1.0F - f6 * f6) * 0.7F);
                    tessellator.setTranslationD(-d * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
                    tessellator.addVertexWithUV(i1 + 0, l1, j1 + 0, 0.0F * f1 + f4, ((float)l1 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 1, l1, j1 + 1, 1.0F * f1 + f4, ((float)l1 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 1, i2, j1 + 1, 1.0F * f1 + f4, ((float)i2 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 0, i2, j1 + 0, 0.0F * f1 + f4, ((float)i2 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 0, l1, j1 + 1, 0.0F * f1 + f4, ((float)l1 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 1, l1, j1 + 0, 1.0F * f1 + f4, ((float)l1 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 1, i2, j1 + 0, 1.0F * f1 + f4, ((float)i2 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 0, i2, j1 + 1, 0.0F * f1 + f4, ((float)i2 * f1) / 8F + f3 * f1 + f5);
                    tessellator.setTranslationD(0.0D, 0.0D, 0.0D);
                    tessellator.draw();
                }
            }

        }

        GL11.glEnable(2884 /*GL_CULL_FACE*/);
        GL11.glDisable(3042 /*GL_BLEND*/);
    }
    }
    
    public void renderSnowMode2(float f)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        World world = mc.theWorld;
        int i = MathHelper.floor_double(entityliving.posX);
        int j = MathHelper.floor_double(entityliving.posY);
        int k = MathHelper.floor_double(entityliving.posZ);
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(2884 /*GL_CULL_FACE*/);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/environment/snow.png"));
        double d = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * (double)f;
        double d1 = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * (double)f;
        double d2 = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * (double)f;
        int l = 5;
        if(mc.gameSettings.fancyGraphics)
        {
            l = 10;
        }
        boolean flag = false;
        for(int i1 = i - l; i1 <= i + l; i1++)
        {
            for(int j1 = k - l; j1 <= k + l; j1++)
            {
            	int k1 = world.findTopSolidBlock(i1, j1);
                if(k1 < 0)
                {
                    k1 = 0;
                }
            	
                int l1 = j - l;
                int i2 = j + l;
                if(l1 < k1)
                {
                    l1 = k1;
                }
                if(i2 < k1)
                {
                    i2 = k1;
                }
                float f1 = 2.0F;
                if(l1 != i2)
                {
                    random.setSeed(i1 * i1 * 3121 /*GL_RGBA_MODE*/ + i1 * 0x2b24abb + j1 * j1 * 0x66397 + j1 * 13761);
                    float f2 = (float)field_1386_j + f;
                    float f3 = ((float)(field_1386_j & 0x1ff) + f) / 512F;
                    float f4 = random.nextFloat() + f2 * 0.01F * (float)random.nextGaussian();
                    float f5 = random.nextFloat() + f2 * (float)random.nextGaussian() * 0.001F;
                    double d3 = (double)((float)i1 + 0.5F) - entityliving.posX;
                    double d4 = (double)((float)j1 + 0.5F) - entityliving.posZ;
                    float f6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4) / (float)l;
                    tessellator.startDrawingQuads();
                    float f7 = world.getLightBrightness(i1, 128, j1);
                    GL11.glColor4f(f7, f7, f7, (1.0F - f6 * f6) * 0.7F);
                    tessellator.setTranslationD(-d * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
                    tessellator.addVertexWithUV(i1 + 0, l1, j1 + 0, 0.0F * f1 + f4, ((float)l1 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 1, l1, j1 + 1, 1.0F * f1 + f4, ((float)l1 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 1, i2, j1 + 1, 1.0F * f1 + f4, ((float)i2 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 0, i2, j1 + 0, 0.0F * f1 + f4, ((float)i2 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 0, l1, j1 + 1, 0.0F * f1 + f4, ((float)l1 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 1, l1, j1 + 0, 1.0F * f1 + f4, ((float)l1 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 1, i2, j1 + 0, 1.0F * f1 + f4, ((float)i2 * f1) / 8F + f3 * f1 + f5);
                    tessellator.addVertexWithUV(i1 + 0, i2, j1 + 1, 0.0F * f1 + f4, ((float)i2 * f1) / 8F + f3 * f1 + f5);
                    tessellator.setTranslationD(0.0D, 0.0D, 0.0D);
                    tessellator.draw();
                }
            }

        }

        GL11.glEnable(2884 /*GL_CULL_FACE*/);
        GL11.glDisable(3042 /*GL_BLEND*/);
    }

    // Snow Mod END
    
    private Minecraft mc;
    private float farPlaneDistance;
    public ItemRenderer itemRenderer;
    private int field_1386_j;
    private Entity field_1385_k;
    private MouseFilter mouseFilterXAxis;
    private MouseFilter mouseFilterYAxis;
    private MouseFilter field_22233_n;
    private MouseFilter field_22232_o;
    private MouseFilter field_22231_p;
    private MouseFilter field_22229_q;
    private float field_22228_r;
    private float field_22227_s;
    private float field_22226_t;
    private float field_22225_u;
    private float field_22224_v;
    private float field_22223_w;
    private float field_22222_x;
    private float field_22221_y;
    private float field_22220_z;
    private float field_22230_A;
    private double cameraZoom;
    private double cameraYaw;
    private double cameraPitch;
    private long prevFrameTime;
    private Random random;
    volatile int field_1394_b;
    volatile int field_1393_c;
    FloatBuffer field_1392_d;
    float fogColorRed;
    float fogColorGreen;
    float fogColorBlue;
    private float field_1382_n;
    private float field_1381_o;
}
