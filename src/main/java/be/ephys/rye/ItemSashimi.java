package be.ephys.rye;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSashimi extends ItemFood {

  public ItemSashimi() {
    super(9, 1.4f, false);

    String name = "sashimi";

    setRegistryName(name);
    setUnlocalizedName(RyeMod.MODID + ":" + name);

    setMaxStackSize(1);
    setCreativeTab(CreativeTabs.FOOD);
  }

  void register(RegistryEvent.Register<Item> event) {
    event.getRegistry().register(this);
  }

  @SideOnly(Side.CLIENT)
  void registerModel() {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }
}