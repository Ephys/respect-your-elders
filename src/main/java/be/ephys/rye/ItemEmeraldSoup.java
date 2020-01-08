package be.ephys.rye;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO texture
// TODO: give back bowl

public class ItemEmeraldSoup extends ItemFood {

  public static Map<String, Biome.SpawnListEntry> instances = new HashMap<>();

  public ItemEmeraldSoup() {
    super(3, 0.6f, false);

    String name = "emeraldsoup";

    setRegistryName(name);
    setUnlocalizedName(RyeMod.MODID + ":" + name);

    setMaxStackSize(1);
    setCreativeTab(CreativeTabs.FOOD);
    setAlwaysEdible();
  }

  // new Biome.SpawnListEntry(EntityEvoker.class, baseSpawnChance, 1, 1)
  public static Biome.SpawnListEntry getInstance(Class<? extends EntityLiving> entityclassIn, int weight, int groupCountMin, int groupCountMax) {
    String key = entityclassIn.getName() + " " + weight + " " + groupCountMin + " " + groupCountMax;

    if (!instances.containsKey(key)) {
      instances.put(key, new Biome.SpawnListEntry(entityclassIn, weight, groupCountMin, groupCountMax));
    }

    return instances.get(key);
  }

  public static double mapLog10(double input, double inputMin, double inputMax, double outputMin, double outputMax) {
    if (input < inputMin) {
      input = inputMin;
    } else if (input > inputMax) {
      input = inputMax;
    }

    // log10 will be at y0 for x1 and y1 for x10 -- perfect for diminishing returns
    // we need to map input to range [1, 10]
    double logInput = map(input, inputMin, inputMax, 1, 10);

    // get a value between 0 and 1 logarithmically (diminishing returns)
    double logOutput = Math.log10(logInput);

    return map(logOutput, 0, 1, outputMin, outputMax);
  }

  /**
   * This function takes an input from range [in_min, in_max] and maps it linearly to range [out_min, out_max]
   * <p>
   * 4 from [0, 10] becomes 140 for [100, 200]
   */
  public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
  }

  @SideOnly(Side.CLIENT)
  void registerModel() {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.DRINK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add(I18n.format(getUnlocalizedName() + ".tooltip"));

    super.addInformation(stack, worldIn, tooltip, flagIn);
  }

  void register(RegistryEvent.Register<Item> event) {
    event.getRegistry().register(this);
    MinecraftForge.EVENT_BUS.register(this);
  }

  // https://stackoverflow.com/questions/7505991/arduino-map-equivalent-function-in-java

  @SubscribeEvent
  public void onPotentialSpawn(WorldEvent.PotentialSpawns event) {
    if (event.getType() != EnumCreatureType.MONSTER) {
      return;
    }

    /*
     * If a player inside a Mansion has the Illager Craze potion effect active,
     * Allow Vindicators to spawn like normal mobs
     * And allow Evokers to spawn like normal mobs if there are less than 4 in the building
     */
    World world = event.getWorld();
    if (!(world instanceof WorldServer)) {
      return;
    }

    BlockPos spawnPos = event.getPos();
    WoodlandMansion mansionGenerator = StructureHelper.getMansionGenerator((WorldServer) world);
    if (mansionGenerator == null) {
      return;
    }

    StructureHelper.initializeStructureData(mansionGenerator, world);
//    StructureStart mansion = StructureHelper.getStructureAt(mansionGenerator, spawnPos);
    StructureStart mansion = StructureHelper.getIncompleteStructureAt(mansionGenerator, world, new ChunkPos(spawnPos));
    if (mansion == null) {
      return;
    }

    // only spawn if there are players in the building with Illager Craze active
    AxisAlignedBB mansionBB = StructureHelper.structBbToAxisAlignedBb(mansion.getBoundingBox());
    List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, mansionBB);
    int crazyPeople = 0;
    for (EntityPlayer player : players) {
      if (player.isSpectator() || player.getActivePotionEffect(ModItems.POTION_ILLAGER_CRAZE) == null) {
        continue;
      }

      crazyPeople++;

      if (crazyPeople >= 5) {
        break;
      }
    }

    if (crazyPeople == 0) {
      return;
    }

    List<EntityEvoker> evokers = world.getEntitiesWithinAABB(EntityEvoker.class, mansionBB);
    // map number of people to the spawn chance using a logarithmic scale (eg. 1 = 30, 2 = 45, etc...)
    int baseSpawnChance = (int) Math.round(mapLog10(crazyPeople, 1, 5, 30, 70));

    System.out.println(crazyPeople + " crazies " + evokers.size() + " evokers");

//    event.getList().clear();

    if (evokers.size() < 4) {
      // int weight, int groupCountMin, int groupCountMax
      event.getList().add(getInstance(EntityEvoker.class, baseSpawnChance, 1, 1));
    }

    event.setResult(Event.Result.ALLOW);
    event.getList().add(getInstance(EntityVindicator.class, baseSpawnChance * 2, 1, 1));
  }

  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
    super.onItemUseFinish(stack, worldIn, entityLiving);
    return new ItemStack(Items.BOWL);
  }
}
