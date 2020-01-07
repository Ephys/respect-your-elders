package be.ephys.rye;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RyeMod.MODID)
public final class ModelHandler {

  @SubscribeEvent
  public static void registerModels(ModelRegistryEvent evt) {
    ModItems.ELDER_CRYSTAL.registerModel();
  }
}
