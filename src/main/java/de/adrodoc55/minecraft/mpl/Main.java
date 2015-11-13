package de.adrodoc55.minecraft.mpl;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.gui.ChainRenderer;

public class Main {

    public static void main(String[] args) {

        File inputDir = new File("C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/methods");
        File outputDir = new File("C:/Users/adrian/Minecraft/MCEdit.v1.4.0.1.Win.64bit/stock-filters");

         File[] inputFiles = inputDir.listFiles();
//        File[] inputFiles = { new File(inputDir, "ACV_createPortalFailed.txt") };
        for (File inputFile : inputFiles) {
            // File inputFile = new File(inputDir,
            // "ACV_validateDirections.txt");
            File outputFile = new File(outputDir, inputFile.getName() + ".py");

            List<Command> commands = readProgramFile(inputFile);

//            JFrame frame = new JFrame(inputFile.getName());
//            // ChainRenderer renderer = new ChainRenderer(commands);
//            // frame.getContentPane().add(renderer, BorderLayout.CENTER);
//            ChainRenderer optimalRenderer = new ChainRenderer(commands);
//            frame.getContentPane().add(optimalRenderer, BorderLayout.EAST);
//            frame.pack();
//            frame.setLocationRelativeTo(null);
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setVisible(true);
            // ChainCalculator calculator = new ChainCalculator(commands,
            // renderer,
            // optimalRenderer);

            ChainCalculator calculator = new ChainCalculator(commands);
            List<Coordinate3D> optimal = calculator.calculateOptimalChain();

//            optimalRenderer.render(optimal);

            StringBuilder sb = new StringBuilder("from pymclevel.entity import TileEntity\n"
                    + "from pymclevel.nbt import TAG_String\n" + "from pymclevel.nbt import TAG_Byte\n" + "\n"
                    + "displayName = 'Generate " + inputFile.getName() + "'\n" + "\n"
                    + "def create_command_block(level, xyz, command, direction='south', mode='chain', conditional=False, auto=True):\n"
                    + "    x, y, z = xyz\n" + "    		\n"
                    + "    idDict = {'impulse' : 137, 'chain' : 211, 'repeat' : 210}\n" + "    blockId = idDict[mode]\n"
                    + "    level.setBlockAt(x, y, z, blockId)\n" + "    \n"
                    + "    damageList = ['down', 'up', 'north', 'south', 'west', 'east']\n"
                    + "    damage = damageList.index(direction)\n" + "    if conditional:\n" + "        damage += 8\n"
                    + "\n" + "    level.setBlockDataAt(x, y, z, damage)\n"
                    + "    control = TileEntity.Create('Control', xyz)\n"
                    + "    control['Command'] = TAG_String(command)\n" + "    control['auto'] = TAG_Byte(auto)\n"
                    + "    level.addTileEntity(control)\n" + "\n" + "def perform(level, box, options):\n");
            String indent = "    ";

            Pattern referencePattern = Pattern.compile("\\$\\{(-?\\d+)\\}");
            for (int i = 0; i < commands.size(); i++) {
                Command command = commands.get(i);
                Coordinate3D coordinate = optimal.get(i);

                if (command != null) {
                    Matcher matcher = referencePattern.matcher(command.getCommand());
                    StringBuffer commandSb = new StringBuffer();
                    while (matcher.find()) {
                        int relative = Integer.parseInt(matcher.group(1));
                        Coordinate3D referenced = optimal.get(i + relative);
                        Coordinate3D relativeCoordinate = referenced.minus(coordinate);
                        matcher.appendReplacement(commandSb, relativeCoordinate.toRelativeString());
                    }
                    matcher.appendTail(commandSb);
                    command.setCommand(commandSb.toString());
                }

                Coordinate3D nextCoordinate = optimal.get(i + 1);
                Coordinate3D directionalCoordinate = nextCoordinate.minus(coordinate);
                Direction direction = Direction.valueOf(directionalCoordinate);

                CommandBlock block = new CommandBlock(command, direction, coordinate);
                sb.append(indent + block.toPython() + "\n");
            }

            outputFile.delete();
            try {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));) {
                writer.write(sb.toString());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static List<Command> readProgramFile(File programFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(programFile))) {
            List<Command> program = new ArrayList<Command>();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                if (line.startsWith("/")) {
                    program.add(new Command(line));
                } else if (line.startsWith("$")) {
                    line = line.substring(1).trim();
                    if (line.equals("pass")) {
                        program.add(null);
                    }
                } else {
                    int start = line.indexOf('/');
                    if (start == -1) {
                        throw new RuntimeException("Folgende Zeile kann nicht interpretiert werden: " + line);
                    }
                    String command = line.substring(start);
                    String configuration = line.substring(0, start).replace(":", "");
                    String[] configs = configuration.split(",");
                    boolean conditional = false;
                    Mode mode = Mode.CHAIN;
                    for (String config : configs) {
                        config = config.trim();
                        switch (config) {
                        case "conditional":
                            conditional = true;
                            break;
                        case "impulse":
                            mode = Mode.IMPULSE;
                            break;
                        case "chain":
                            mode = Mode.CHAIN;
                            break;
                        case "repeat":
                            mode = Mode.REPEAT;
                            break;

                        default:
                            throw new IllegalArgumentException("Umbekannte Konfiguration: " + config);
                        }
                    }
                    program.add(new Command(command, conditional, mode));
                }
            }
            return program;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
