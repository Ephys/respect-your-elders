package be.ephys.caramella;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
  modid = CaramellaMod.MODID,
  version = CaramellaMod.VERSION,
  name = CaramellaMod.NAME,
  dependencies="required-after:cookiecore@[2.0.0,);",
  certificateFingerprint = "@FINGERPRINT@"
)
public class CaramellaMod {
    public static final String MODID = "caramella";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "Caramella";

    @Mod.Instance
    public static CaramellaMod instance;

    @SidedProxy(clientSide = "be.ephys.caramella.ClientProxy", serverSide = "be.ephys.caramella.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigSynchronizer.synchronizeConfig(event);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}
