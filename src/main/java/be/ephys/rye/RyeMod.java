package be.ephys.rye;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
  modid = RyeMod.MODID,
  version = RyeMod.VERSION,
  name = RyeMod.NAME,
  dependencies = "required-after:cookiecore@[2.0.0,);",
  certificateFingerprint = "@FINGERPRINT@"
)
public class RyeMod {
  public static final String MODID = "respect-your-elders";
  public static final String VERSION = "@VERSION@";
  public static final String NAME = "Respect Your Elders";

  @Mod.Instance
  public static RyeMod instance;

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    ConfigSynchronizer.synchronizeConfig(event);
  }

  @Mod.EventHandler
  public void serverStarting(FMLServerStartingEvent event) {
    event.registerServerCommand(new GenerateStructureCommand());
  }
}
