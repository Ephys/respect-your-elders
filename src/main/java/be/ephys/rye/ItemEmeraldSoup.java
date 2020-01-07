package be.ephys.rye;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEmeraldSoup extends ItemFood {

  public ItemEmeraldSoup() {
    super(3, 0.6f, false);

    String name = "emeraldsoup";

    setRegistryName(name);
    setUnlocalizedName(RyeMod.MODID + ":" + name);

    setMaxStackSize(1);
    setCreativeTab(CreativeTabs.FOOD);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add(I18n.format(getUnlocalizedName() + ".tooltip"));

    super.addInformation(stack, worldIn, tooltip, flagIn);
  }

  void register(RegistryEvent.Register<Item> event) {
    event.getRegistry().register(this);
  }

  @SideOnly(Side.CLIENT)
  void registerModel() {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }
}
