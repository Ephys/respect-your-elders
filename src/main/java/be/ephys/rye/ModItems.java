package be.ephys.rye;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RyeMod.MODID)
public class ModItems {
  public static ItemElderCrystal ELDER_CRYSTAL;

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    ELDER_CRYSTAL = new ItemElderCrystal();
    ELDER_CRYSTAL.register(event);
  }
}
