package be.ephys.rye;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = RyeMod.MODID)
public class ModItems {
  public static ItemElderCrystal ELDER_CRYSTAL;
  public static ItemEmeraldSoup EMERALD_SOUP;
  public static ItemSashimi SASHIMI;

  public static Potion POTION_ILLAGER_CRAZE;

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    ELDER_CRYSTAL = new ItemElderCrystal();
    ELDER_CRYSTAL.register(event);

    EMERALD_SOUP = new ItemEmeraldSoup();
    EMERALD_SOUP.register(event);

    SASHIMI = new ItemSashimi();
    SASHIMI.register(event);
  }

  @SubscribeEvent
  public static void registerPotion(RegistryEvent.Register<Potion> event) {
    // TODO color
    POTION_ILLAGER_CRAZE = new RyePotion("illager_craze", true, 0xff0000, RyePotion.INDEX_ILLAGER_CRAZE);
    EMERALD_SOUP.setPotionEffect(new PotionEffect(ModItems.POTION_ILLAGER_CRAZE, 12000 /* 600s */), 1F);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public static void registerModels(ModelRegistryEvent evt) {
    ELDER_CRYSTAL.registerModel();
    EMERALD_SOUP.registerModel();
    SASHIMI.registerModel();
  }
}