package be.ephys.rye;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
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
import java.util.Random;

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
        playerIn.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".outside-monument"), true);
      } else {
        AxisAlignedBB monumentBB = StructureHelper.structBbToAxisAlignedBb(monument.getBoundingBox());
        List<EntityElderGuardian> elders = worldIn.getEntitiesWithinAABB(EntityElderGuardian.class, monumentBB);

        if (elders.size() > 2) {
          worldIn.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.5F, 1.0F);
          playerIn.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".too-many-elders"), true);
        } else {

          if (!playerIn.capabilities.isCreativeMode) {
            itemstack.shrink(1);
          }

          worldIn.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.5F, 1.5F);

          List<Piece> elderSpawnRooms = findElderSpawnRooms(monument.getComponents());

          // teleport players outside
          List<EntityPlayer> players = worldIn.getEntitiesWithinAABB(EntityPlayer.class, monumentBB);

          for (Piece spawnRoom: elderSpawnRooms) {
            if (spawnRoom instanceof EntryRoom) {

              for (EntityPlayer player : players) {
                player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 300));

                Random random = player.getRNG();
                double d0 = player.posX;
                double d1 = player.posY;
                double d2 = player.posZ;

                player.setPositionAndUpdate(
                  (spawnRoom.getBoundingBox().minX + spawnRoom.getBoundingBox().maxX) / 2d,
                  (spawnRoom.getBoundingBox().minY + spawnRoom.getBoundingBox().maxY) / 2d,
                  (spawnRoom.getBoundingBox().minZ + spawnRoom.getBoundingBox().maxZ) / 2d
                );

                worldIn.playSound(null, player.getPosition(), SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT, SoundCategory.HOSTILE, 1.0F, 0.45F);
                worldIn.playSound(null, player.getPosition(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.5F, 0.5F);

                for (int j = 0; j < 128; ++j) {
                  double d6 = (double)j / 127.0D;
                  float f = (random.nextFloat() - 0.5F) * 0.2F;
                  float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                  float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                  double d3 = d0 + (player.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double)player.width * 2.0D;
                  double d4 = d1 + (player.posY - d1) * d6 + random.nextDouble() * (double)player.height;
                  double d5 = d2 + (player.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double)player.width * 2.0D;
                  worldIn.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, f, f1, f2);
                }
              }

              break;
            }
          }

          for (Piece spawnRoom: elderSpawnRooms) {
            if (spawnRoom instanceof Penthouse) {
              this.spawnElder(worldIn, spawnRoom, 6, 1, 6);
            }

            if (spawnRoom instanceof WingRoom) {
              // mainDesign == 0
              int mainDesign = StructureHelper.getOceanWingRoomType((WingRoom) spawnRoom);

              if (mainDesign == 0) {
                this.spawnElder(worldIn, spawnRoom, 11, 2, 16);
              }

              if (mainDesign == 1) {
                this.spawnElder(worldIn, spawnRoom, 11, 5, 13);
              }
            }
          }
        }
      }
    }

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

      if (component instanceof Penthouse || component instanceof WingRoom || component instanceof EntryRoom) {
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

    StructureStart structureStart = StructureHelper.getStructureAt(oceanMonumentGenerator, playerPosition);
    if (!(structureStart instanceof StructureOceanMonument.StartMonument)) {
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
