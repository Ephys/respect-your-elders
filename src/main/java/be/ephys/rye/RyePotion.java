package be.ephys.rye;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class RyePotion extends Potion {

  public static final int INDEX_ILLAGER_CRAZE = 0;

  public static final ResourceLocation POTION_ICON_TEXTURE = new ResourceLocation(RyeMod.MODID, "textures/gui/potions.png");

  private final int iconX;
  private final int iconY;

  public RyePotion(String name, boolean badEffect, int color, int iconIndex) {
    super(badEffect, color);
    iconX = iconIndex % 8;
    iconY = iconIndex / 8;
    setPotionName(RyeMod.MODID + ":" + name);
    setRegistryName(new ResourceLocation(RyeMod.MODID + ":" + name));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void renderHUDEffect(@Nonnull PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
    GlStateManager.color(1f, 1f, 1f, alpha);
    Minecraft.getMinecraft().renderEngine.bindTexture(POTION_ICON_TEXTURE);
    gui.drawTexturedModalRect(x + 3, y + 3, iconX * 18, 198 + iconY * 18, 18, 18);
    GlStateManager.color(1f, 1f, 1f);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
    Minecraft.getMinecraft().renderEngine.bindTexture(POTION_ICON_TEXTURE);

    // InventoryEffectRenderer.drawActivePotionEffects
    gui.drawTexturedModalRect(x + 6, y + 7, iconX * 18, iconY * 18, 18, 18);
  }
}
