package de.adrodoc55.minecraft.mpl;

import java.util.Map;

import de.adrodoc55.minecraft.Coordinate3D;

public class Program {
    // private static final Pattern includePattern = Pattern
    // .compile("^include\\s*\\(\\s*\"(.*)\"\\s*,\\s*at\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)\\s*\\)$");

    private final Map<Coordinate3D, CommandChain> chains;

    public Program(Map<Coordinate3D, CommandChain> chains) {
        this.chains = chains;
    }

    // public Program(File programFile) {
    // chains = readProgramFile(programFile);
    // }

    public Map<Coordinate3D, CommandChain> getChains() {
        return chains;
    }

    // private static Program compile(File programFile) throws IOException {
    // ANTLRInputStream input = new ANTLRInputStream(
    // Files.newBufferedReader(programFile.toPath()));
    // // return compile(programFile.getName(), input);
    // return null;
    // }

    // private static CommandChain compile(String programName,
    // ANTLRInputStream input) {
    // MplLexer lexer = new MplLexer(input);
    // TokenStream tokens = new CommonTokenStream(lexer);
    // MplParser parser = new MplParser(tokens);
    //
    // ProgramContext program = parser.program();
    //
    // List<Command> commands = new LinkedList<Command>();
    //
    // List<LineContext> line = program.line();
    // for (LineContext lineContext : line) {
    // IncludeDeclarationContext includeDeclaration = lineContext
    // .includeDeclaration();
    // // if (includeDeclaration != null) {
    // // String includeString = includeDeclaration.STRING().getText();
    // // IncludeAtContext includeAt = includeDeclaration.includeAt();
    // // IncludeMaxContext includeMax = includeDeclaration.includeMax();
    // //
    // // }
    // if (lineContext.skipDeclaration() != null) {
    // commands.add(null);
    // continue;
    // }
    // TerminalNode command = lineContext.COMMAND();
    // if (command == null) {
    // continue;
    // }
    //
    // addCommand(command.getText(), lineContext, commands);
    // continue;
    // }
    // return new CommandChain(programName, commands);
    // }
    //
    // private static void addCommand(String command, LineContext lineContext,
    // List<Command> commands) {
    //
    // ModifierListContext modifierList = lineContext.modifierList();
    // if (modifierList == null) {
    // commands.add(new Command(command));
    // return;
    // }
    //
    // Mode mode = Mode.CHAIN;
    // ModusContext modus = modifierList.modus();
    // if (modus != null) {
    // String modusText = modus.getText();
    // mode = Mode.valueOf(modusText.toUpperCase());
    // }
    //
    // boolean conditional = false;
    // if (modifierList.CONDITIONAL() != null) {
    // conditional = true;
    // }
    //
    // boolean needsRedstone = (mode != Mode.CHAIN);
    // AutoContext auto = modifierList.auto();
    // if (auto != null) {
    // if (auto.NEEDS_REDSTONE() != null) {
    // needsRedstone = true;
    // } else if (auto.ALWAYS_ACTIVE() != null) {
    // needsRedstone = false;
    // }
    // }
    //
    // commands.add(new Command(command, mode, conditional, needsRedstone));
    // }

    /*
     * private static Map<Coordinate3D, CommandChain> readProgramFile(File
     * programFile) { return readProgramFile(programFile, new Coordinate3D()); }
     *
     * private static Map<Coordinate3D, CommandChain> readProgramFile(File
     * programFile, Coordinate3D at) { try (BufferedReader reader = new
     * BufferedReader(new FileReader(programFile))) { Map<Coordinate3D,
     * CommandChain> methods = new HashMap<Coordinate3D, CommandChain>();
     * List<Command> commands = new ArrayList<Command>(); while (true) { String
     * line = reader.readLine(); if (line == null) { break; } line =
     * line.trim(); if (line.isEmpty() || line.startsWith("#") ||
     * line.startsWith("//")) { continue; }
     *
     * if (line.startsWith("/")) { commands.add(new Command(line)); } else if
     * (line.startsWith("$")) { String command = line.substring(1).trim();
     *
     * Matcher includeMatcher = includePattern.matcher(command); if
     * (command.equals("pass")) { commands.add(null); } else if
     * (includeMatcher.find()) { String fileName = includeMatcher.group(1); int
     * x = Integer.parseInt(includeMatcher.group(2)); int y =
     * Integer.parseInt(includeMatcher.group(3)); int z =
     * Integer.parseInt(includeMatcher.group(4));
     *
     * File file = new File(programFile.getParentFile(), fileName);
     * Map<Coordinate3D, CommandChain> includedFile = readProgramFile(file, new
     * Coordinate3D(x, y, z)); methods.putAll(includedFile); } else { throw new
     * RuntimeException("Folgende Zeile kann nicht interpretiert werden: " +
     * line); } } else { int start = line.indexOf('/'); if (start == -1) { throw
     * new RuntimeException("Folgende Zeile kann nicht interpretiert werden: " +
     * line); } String command = line.substring(start); String configuration =
     * line.substring(0, start).replace(":", ""); String[] configs =
     * configuration.split(","); boolean conditional = false; Mode mode =
     * Mode.CHAIN; for (String config : configs) { config = config.trim();
     * switch (config) { case "conditional": conditional = true; break; case
     * "impulse": mode = Mode.IMPULSE; break; case "chain": mode = Mode.CHAIN;
     * break; case "repeat": mode = Mode.REPEAT; break;
     *
     * default: throw new IllegalArgumentException("Umbekannte Konfiguration: "
     * + config); } } commands.add(new Command(command, conditional, mode)); } }
     * methods.put(at, new CommandChain(programFile.getName(), commands));
     * return methods; } catch (IOException ex) { throw new
     * RuntimeException(ex); } }
     */
}
