package be.ephys.rye;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RyeItem extends Item {
  public RyeItem(String name) {
    super();

    setRegistryName(name);
    setUnlocalizedName(RyeMod.MODID + ":" + name);
  }

  void register(RegistryEvent.Register<Item> event) {
    event.getRegistry().register(this);
  }

  @SideOnly(Side.CLIENT)
  void registerModel() {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }
}
