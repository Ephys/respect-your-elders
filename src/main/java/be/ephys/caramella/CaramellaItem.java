package be.ephys.caramella;

import net.minecraft.item.Item;

public class CaramellaItem extends Item {
  public CaramellaItem(String name) {
    super();

    setRegistryName(name);
    setUnlocalizedName(CaramellaMod.MODID + ":" + name);
  }
}
