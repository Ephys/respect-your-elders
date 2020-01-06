package be.ephys.caramella;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = CaramellaMod.MODID)
public class ModItems {
  public static ItemBossInvoker BOSS_INVOKER;

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    BOSS_INVOKER = new ItemBossInvoker();
    BOSS_INVOKER.register(event);
  }
}
