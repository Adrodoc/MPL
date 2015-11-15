package de.adrodoc55.minecraft.mpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.Command.Mode;

public class Program {
    private static final Pattern includePattern = Pattern.compile(
            "^include\\s*\\(\\s*\"(.*)\"\\s*,\\s*at\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)\\s*\\)$");

    private final Map<Coordinate3D, CommandChain> chains;

    public Program(File programFile) {
        chains = readProgramFile(programFile);
    }

    private static Map<Coordinate3D, CommandChain> readProgramFile(File programFile) {
        return readProgramFile(programFile, new Coordinate3D());
    }

    private static Map<Coordinate3D, CommandChain> readProgramFile(File programFile, Coordinate3D at) {
        try (BufferedReader reader = new BufferedReader(new FileReader(programFile))) {
            Map<Coordinate3D, CommandChain> methods = new HashMap<Coordinate3D, CommandChain>();
            List<Command> commands = new ArrayList<Command>();
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
                    commands.add(new Command(line));
                } else if (line.startsWith("$")) {
                    String command = line.substring(1).trim();

                    Matcher includeMatcher = includePattern.matcher(command);
                    if (command.equals("pass")) {
                        commands.add(null);
                    } else if (includeMatcher.find()) {
                        String fileName = includeMatcher.group(1);
                        int x = Integer.parseInt(includeMatcher.group(2));
                        int y = Integer.parseInt(includeMatcher.group(3));
                        int z = Integer.parseInt(includeMatcher.group(4));

                        File file = new File(programFile.getParentFile(), fileName);
                        Map<Coordinate3D, CommandChain> includedFile = readProgramFile(file, new Coordinate3D(x, y, z));
                        methods.putAll(includedFile);
                    } else {
                        throw new RuntimeException("Folgende Zeile kann nicht interpretiert werden: " + line);
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
                    commands.add(new Command(command, conditional, mode));
                }
            }
            methods.put(at, new CommandChain(programFile.getName(), commands));
            return methods;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Map<Coordinate3D, CommandChain> getChains() {
        return chains;
    }

}
