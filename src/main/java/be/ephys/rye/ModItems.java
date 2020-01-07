package be.ephys.rye;

import net.minecraft.item.Item;
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

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    ELDER_CRYSTAL = new ItemElderCrystal();
    ELDER_CRYSTAL.register(event);

    EMERALD_SOUP = new ItemEmeraldSoup();
    EMERALD_SOUP.register(event);

    SASHIMI = new ItemSashimi();
    SASHIMI.register(event);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public static void registerModels(ModelRegistryEvent evt) {
    ModItems.ELDER_CRYSTAL.registerModel();
    ModItems.EMERALD_SOUP.registerModel();
    ModItems.SASHIMI.registerModel();
  }
}