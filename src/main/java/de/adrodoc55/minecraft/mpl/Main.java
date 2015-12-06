package de.adrodoc55.minecraft.mpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.antlr.MplCompiler;
import de.adrodoc55.minecraft.mpl.chain_computing.ChainComputer;
import de.adrodoc55.minecraft.mpl.chain_computing.IterativeChainComputer;

public class Main {

    private static final String INDENT = "    ";

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Expected exactly two Arguments: imputFile, outputDir");
        }
        File inputFile = new File(args[0]);
        File outputDir = new File(args[1]);
        File outputFile = new File(outputDir, inputFile.getName() + ".py");
        main(inputFile, outputFile);
    }

    private static void main(File inputFile, File outputFile)
            throws IOException {
        StringBuilder python = new StringBuilder(getPythonHeader(inputFile));

        Program program = MplCompiler.compile(inputFile);
        for (CommandChain chain : program.getChains()) {
            ChainComputer calculator = new IterativeChainComputer();
            CommandBlockChain optimal = calculator.computeOptimalChain(chain);
            List<CommandBlock> commandBlocks = optimal.getCommandBlocks();

            insertRelativeCoordinates(commandBlocks);
            for (CommandBlock current : commandBlocks) {
                python.append(INDENT + current.toPython() + "\n");
            }
        }

        outputFile.delete();
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                outputFile));) {
            writer.write(python.toString());
        }
    }

    private static void insertRelativeCoordinates(
            List<CommandBlock> commandBlocks) {
        for (int i = 0; i < commandBlocks.size(); i++) {
            CommandBlock current = commandBlocks.get(i);
            if (current.toCommand() == null) {
                continue;
            }

            Pattern referencePattern = Pattern.compile("\\$\\{(-?\\d+)\\}");
            if (current != null) {
                Matcher matcher = referencePattern
                        .matcher(current.getCommand());
                StringBuffer commandSb = new StringBuffer();
                while (matcher.find()) {
                    int relative = Integer.parseInt(matcher.group(1));
                    Coordinate3D referenced = commandBlocks.get(i + relative)
                            .getCoordinate();
                    Coordinate3D relativeCoordinate = referenced.minus(current
                            .getCoordinate());
                    matcher.appendReplacement(commandSb,
                            relativeCoordinate.toRelativeString());
                }
                matcher.appendTail(commandSb);
                current.setCommand(commandSb.toString());
            }

        }
    }

    private static String getPythonHeader(File inputFile) {
        String pythonHeader = "from pymclevel.entity import TileEntity\n"
                + "from pymclevel.nbt import TAG_String\n"
                + "from pymclevel.nbt import TAG_Byte\n" + "\n"
                + "displayName = 'Generate "
                + inputFile.getName()
                + "'\n"
                + "\n"
                + "def create_command_block(level, xyz, command, direction='south', mode='chain', conditional=False, auto=True):\n"
                + "    x, y, z = xyz\n"
                + "    		\n"
                + "    idDict = {'impulse' : 137, 'chain' : 211, 'repeat' : 210}\n"
                + "    blockId = idDict[mode]\n"
                + "    level.setBlockAt(x, y, z, blockId)\n"
                + "    \n"
                + "    damageList = ['down', 'up', 'north', 'south', 'west', 'east']\n"
                + "    damage = damageList.index(direction)\n"
                + "    if conditional:\n"
                + "        damage += 8\n"
                + "\n"
                + "    level.setBlockDataAt(x, y, z, damage)\n"
                + "    control = TileEntity.Create('Control', xyz)\n"
                + "    control['Command'] = TAG_String(command)\n"
                + "    control['auto'] = TAG_Byte(auto)\n"
                + "    level.addTileEntity(control)\n"
                + "\n"
                + "def perform(level, box, options):\n";
        return pythonHeader;
    }

}
