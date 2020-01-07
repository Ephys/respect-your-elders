package be.ephys.rye;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.gen.structure.StructureOceanMonument.StartMonument;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.gen.structure.StructureOceanMonumentPieces.*;

public class ItemElderCrystal extends RyeItem {

  public ItemElderCrystal() {
    super("eldercrystal");

    setMaxStackSize(8);
    setCreativeTab(CreativeTabs.MISC);
  }

  void register(RegistryEvent.Register<Item> event) {
    event.getRegistry().register(this);
  }

  @SideOnly(Side.CLIENT)
  void registerModel() {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }

  /**
   * Called when the equipped item is right clicked.
   */
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);

    if (!worldIn.isRemote) {
      StartMonument monument = getOceanMonument((WorldServer) worldIn, playerIn);

      if (monument == null) {
        playerIn.sendStatusMessage(new TextComponentString("You need to be inside an ocean monument"), true);
      } else {
        List<Piece> elderSpawnRooms = findElderSpawnRooms(monument.getComponents());

        for (Piece spawnRoom: elderSpawnRooms) {
          System.out.println(spawnRoom);

          if (spawnRoom instanceof Penthouse) {
//          monument.spawn
            this.spawnElder(worldIn, spawnRoom, 6, 1, 6);
          }

          if (spawnRoom instanceof WingRoom) {

          }
        }
      }
//      AxisAlignedBB monumentBB =
//      worldIn.getEntitiesWithinAABB(EntityElderGuardian.class, monument.getBoundingBox());
    }

    // TODO: Used outside: you feel drawn to explore the sea
    // TODO: consume item
    //   Play Elder Guardian ambiant sounds
    //   Summon
    //   Teleport player to EntryRoom.class
    // TODO: no elder if there is already one?
    // TODO: no elder if no water
    //   Spawn elder in all 3 rooms
//    if (!playerIn.capabilities.isCreativeMode) {
//      itemstack.shrink(1);
//    }

    return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
  }

  private boolean spawnElder(World worldIn, Piece spawnRoom, int p_175817_3_, int p_175817_4_, int p_175817_5_) {

    // public net.minecraft.world.gen.structure.StructureOceanMonumentPieces$Piece func_175817_a(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;III)Z #spawnElder
    Method spawnElderMethod = ObfuscationReflectionHelper.findMethod(
      // owner class
      Piece.class,

      // srg method name for spawnElder
      "func_175817_a",

      // return type
      Boolean.TYPE,

      // input param type
      World.class,
      StructureBoundingBox.class,
      Integer.TYPE,
      Integer.TYPE,
      Integer.TYPE
    );

    spawnElderMethod.setAccessible(true);
    try {
      return (boolean) spawnElderMethod.invoke(
        spawnRoom,
        worldIn,
        spawnRoom.getBoundingBox(),
        p_175817_3_,
        p_175817_4_,
        p_175817_5_
      );
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();

      return false;
    }
  }

  private List<Piece> findElderSpawnRooms(List<? extends StructureComponent> components) {
    List<Piece> pieces = new ArrayList<>();

    for (StructureComponent component: components) {
      if (component instanceof MonumentBuilding) {
        // public net.minecraft.world.gen.structure.StructureOceanMonumentPieces$MonumentBuilding field_175843_q #childPieces
        List<Piece> childPieces = ObfuscationReflectionHelper.getPrivateValue(
          MonumentBuilding.class,
          (MonumentBuilding) component,
          "field_175843_q"
        );

        pieces.addAll(findElderSpawnRooms(childPieces));
      }

      if (component instanceof Penthouse || component instanceof WingRoom) {
        pieces.add((Piece) component);
      }
    }

    return pieces;
  }

  private StartMonument getOceanMonument(WorldServer world, EntityPlayer player) {
    IChunkGenerator chunkGenerator = world.getChunkProvider().chunkGenerator;

    // sadly IChunkGenerator doesn't have a map of all
    if (!(chunkGenerator instanceof ChunkGeneratorOverworld)) {
      return null;
    }

    ChunkGeneratorOverworld overworldGenerator = (ChunkGeneratorOverworld) chunkGenerator;

    // get "oceanMonumentGenerator" private field
    StructureOceanMonument oceanMonumentGenerator = ObfuscationReflectionHelper.getPrivateValue(ChunkGeneratorOverworld.class, overworldGenerator, "field_185980_B");

    BlockPos playerPosition = player.getPosition();
    if (!oceanMonumentGenerator.isInsideStructure(playerPosition)) {
      return null;
    }

    // get "getStructureAt" private method
    // public net.minecraft.world.gen.structure.MapGenStructure func_175797_c(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/gen/structure/StructureStart; #getStructureAt
    Method getStructureAt = ObfuscationReflectionHelper.findMethod(
      // owner class
      MapGenStructure.class,

      // srg method name
      "func_175797_c",

      // return type
      StructureStart.class,

      // input param type
      BlockPos.class
    );

    getStructureAt.setAccessible(true);
    StructureStart structureStart;
    try {
      structureStart = (StructureStart) getStructureAt.invoke(oceanMonumentGenerator, playerPosition);

      if (!(structureStart instanceof StartMonument)) {
        return null;
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();

      return null;
    }

    // Ocean monument we get from above is devoid of any data
    // we need to rebuild its information to find the room.
    int chunkX = structureStart.getChunkPosX();
    int chunkZ = structureStart.getChunkPosZ();

    // public net.minecraft.world.gen.structure.StructureOceanMonument func_75049_b(II)Lnet/minecraft/world/gen/structure/StructureStart;
    // = getStructureStart
    Method rebuildStructureStartMethod = ObfuscationReflectionHelper.findMethod(
      // owner class
      StructureOceanMonument.class,

      // srg method name
      "func_75049_b",

      // return type
      StructureStart.class,

      // input param type
      Integer.TYPE,
      Integer.TYPE
    );

    rebuildStructureStartMethod.setAccessible(true);
    try {
      return (StartMonument) rebuildStructureStartMethod.invoke(oceanMonumentGenerator, chunkX, chunkZ);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }
}
