package be.ephys.rye;

import net.minecraft.item.Item;

public class RyeItem extends Item {
  public RyeItem(String name) {
    super();

    setRegistryName(name);
    setUnlocalizedName(RyeMod.MODID + ":" + name);
  }
}
