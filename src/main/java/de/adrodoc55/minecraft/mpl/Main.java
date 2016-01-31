package de.adrodoc55.minecraft.mpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.adrodoc55.minecraft.mpl.antlr.CompilationFailedException;

public class Main {

  private static final String INDENT = "    ";

  public static void main(String[] args) throws IOException, CompilationFailedException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Expected exactly two Arguments: imputFile, outputDir");
    }
    File inputFile = new File(args[0]);
    File outputDir = new File(args[1]);
    File outputFile = new File(outputDir, inputFile.getName() + ".py");
    main(inputFile, outputFile);
  }

  public static void main(File inputFile, File outputFile)
      throws IOException, CompilationFailedException {
    StringBuilder python = new StringBuilder(getPythonHeader(inputFile));

    List<CommandBlockChain> chains = MplCompiler.compile(inputFile);
    for (CommandBlockChain chain : chains) {
      List<CommandBlock> blocks = chain.getCommandBlocks();
      for (CommandBlock block : blocks) {
        python.append(INDENT + block.toPython() + "\n");
      }
    }

    outputFile.delete();
    outputFile.getParentFile().mkdirs();
    outputFile.createNewFile();
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));) {
      writer.write(python.toString());
    }
  }

  private static String getPythonHeader(File inputFile) {
    String pythonHeader = "from pymclevel.entity import TileEntity\n"
        + "from pymclevel.nbt import TAG_String\n" + "from pymclevel.nbt import TAG_Byte\n" + "\n"
        + "displayName = 'Generate " + inputFile.getName() + "'\n" + "\n"
        + "def create_command_block(level, xyz, command, direction='south', mode='chain', conditional=False, auto=True):\n"
        + "    x, y, z = xyz\n" + "    		\n"
        + "    idDict = {'impulse' : 137, 'chain' : 211, 'repeat' : 210}\n"
        + "    blockId = idDict[mode]\n" + "    level.setBlockAt(x, y, z, blockId)\n" + "    \n"
        + "    damageList = ['down', 'up', 'north', 'south', 'west', 'east']\n"
        + "    damage = damageList.index(direction)\n" + "    if conditional:\n"
        + "        damage += 8\n" + "\n" + "    level.setBlockDataAt(x, y, z, damage)\n"
        + "    control = TileEntity.Create('Control', xyz)\n"
        + "    control['Command'] = TAG_String(command)\n"
        + "    control['auto'] = TAG_Byte(auto)\n" + "    level.addTileEntity(control)\n" + "\n"
        + "def perform(level, box, options):\n";
    return pythonHeader;
  }

}
