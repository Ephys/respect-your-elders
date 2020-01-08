package be.ephys.rye;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.WoodlandMansion;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GenerateStructureCommand extends CommandBase {

  // TODO: disabled by default

  private static String COMMAND_LOCALE_KEY = "commands." + RyeMod.MODID + ":genstruct";

  @Override
  public String getName() {
    return "genstruct";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return COMMAND_LOCALE_KEY + ".usage";
  }

  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
  {
    return args.length == 1 ? getListOfStringsMatchingLastWord(args, "Mansion") : Collections.emptyList();
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

    switch (args[0]) {
      case "Mansion":
        generateMansion(sender);
        return;

      default:
        throw new WrongUsageException(COMMAND_LOCALE_KEY + ".unsupported-structure");
    }
  }

  private void generateMansion(ICommandSender sender) throws CommandException {
    WorldServer world = (WorldServer) sender.getEntityWorld();
    WoodlandMansion woodlandMansionGenerator = StructureHelper.getMansionGenerator(world);

    if (woodlandMansionGenerator == null) {
      throw new WrongUsageException(COMMAND_LOCALE_KEY + ".overworld");
    }

    // replicate what's done in ChunkGeneratorOverworld.populate
    // generate a new seeded RNG for this specific structure
    BlockPos playerPos = sender.getPosition();
    ChunkPos chunkPos = new ChunkPos(playerPos);

    int chunkX = chunkPos.x;
    int chunkZ = chunkPos.z;

    long worldSeed = world.getSeed();
    Random rand = new Random(worldSeed);
    long k = rand.nextLong() / 2L * 2L + 1L;
    long l = rand.nextLong() / 2L * 2L + 1L;
    rand.setSeed((long)chunkX * k + (long)chunkZ * l ^ worldSeed);

    // if a Mansion already exists there, reset it or continue building it
    StructureStart mansionStart = StructureHelper.getIncompleteStructureAt(woodlandMansionGenerator, world, chunkPos);
    if (mansionStart == null) {
      // else create a new Mansion
      mansionStart = StructureHelper.getMansionStructureStart(woodlandMansionGenerator, chunkX, chunkZ);
      // TODO: if mansionStart will intersect with another structure -> reject
      StructureHelper.getStructureMap(woodlandMansionGenerator).put(ChunkPos.asLong(chunkX, chunkZ), mansionStart);
    }

    mansionStart.generateStructure(world, rand, mansionStart.getBoundingBox());

    StructureHelper.setStructureStart(woodlandMansionGenerator, chunkX, chunkZ, mansionStart);
  }
}
