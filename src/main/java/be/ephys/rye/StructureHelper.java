package be.ephys.rye;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class StructureHelper {

  public static StructureStart getStructureAt(MapGenStructure structure, BlockPos pos) {
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

    try {
      return (StructureStart) getStructureAt.invoke(structure, pos);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static AxisAlignedBB structBbToAxisAlignedBb(StructureBoundingBox monumentBB) {
    return new AxisAlignedBB(monumentBB.minX, monumentBB.minY, monumentBB.minZ, monumentBB.maxX, monumentBB.maxY, monumentBB.maxZ);
  }

  public static int getOceanWingRoomType(StructureOceanMonumentPieces.WingRoom wingRoom) {
    // public net.minecraft.world.gen.structure.StructureOceanMonumentPieces$WingRoom field_175834_o #mainDesign
    return ObfuscationReflectionHelper.getPrivateValue(StructureOceanMonumentPieces.WingRoom.class, wingRoom, "field_175834_o");
  }

  public static Long2ObjectMap<StructureStart> getStructureMap(MapGenStructure structure) {
    // public net.minecraft.world.gen.structure.MapGenStructure field_75053_d #structureMap
    return ObfuscationReflectionHelper.getPrivateValue(MapGenStructure.class, structure, "field_75053_d");
  }

  public static synchronized StructureStart getIncompleteStructureAt(MapGenStructure structure, World world, ChunkPos chunkPos) {
    StructureHelper.initializeStructureData(structure, world);

    int i = (chunkPos.x << 4) + 8;
    int j = (chunkPos.z << 4) + 8;

    for (StructureStart structurestart : getStructureMap(structure).values()) {
      if (structurestart.getBoundingBox().intersectsWith(i, j, i + 15, j + 15)) {
        return structurestart;
      }
    }

    System.out.println("no");
    return null;
  }

  public static void initializeStructureData(MapGenStructure structure, World world) {
    // public net.minecraft.world.gen.structure.MapGenStructure func_143027_a(Lnet/minecraft/world/World;)V #initializeStructureData

    Method initializeStructureData = ObfuscationReflectionHelper.findMethod(
      // owner class
      MapGenStructure.class,

      // srg method name
      "func_143027_a",

      // return type
      Void.TYPE,

      // input param type
      World.class
    );

    initializeStructureData.setAccessible(true);

    try {
      initializeStructureData.invoke(structure, world);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setStructureStart(MapGenStructure structure, int chunkX, int chunkZ, StructureStart structureStart) {
    // public net.minecraft.world.gen.structure.MapGenStructure func_143026_a(IILnet/minecraft/world/gen/structure/StructureStart;)V #setStructureStart

    Method setStructureStart = ObfuscationReflectionHelper.findMethod(
      // owner class
      MapGenStructure.class,

      // srg method name
      "func_143026_a",

      // return type
      Void.TYPE,

      // input param type
      Integer.TYPE,
      Integer.TYPE,
      StructureStart.class
    );

    setStructureStart.setAccessible(true);

    try {
      setStructureStart.invoke(structure, chunkX, chunkZ, structureStart);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static WoodlandMansion.Start getMansionStructureStart(WoodlandMansion mansion, int chunkX, int chunkZ) {
    Method getStructureStart = ObfuscationReflectionHelper.findMethod(
      // owner class
      WoodlandMansion.class,

      // srg method name
      "func_75049_b",

      // return type
      StructureStart.class,

      // input param type
      Integer.TYPE,
      Integer.TYPE
    );

    getStructureStart.setAccessible(true);

    try {
      return (WoodlandMansion.Start) getStructureStart.invoke(mansion, chunkX, chunkZ);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
