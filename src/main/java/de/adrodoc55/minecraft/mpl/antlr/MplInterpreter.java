package de.adrodoc55.minecraft.mpl.antlr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.CommandChain;
import de.adrodoc55.minecraft.mpl.CompilerException;
import de.adrodoc55.minecraft.mpl.antlr.CommandBuffer.Conditional;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.AutoContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ConditionalContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.FileContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ImportDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.InstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.InterceptContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModusContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.NotifyDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProcessContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProjectContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkipContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkriptFileContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.StartContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.StopContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.UninstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.WaitforContext;

public class MplInterpreter extends MplBaseListener {

  private static final String NOTIFY = "_NOTIFY";

  public static MplInterpreter interpret(File programFile) throws IOException {
    FileContext ctx = parse(programFile);
    MplInterpreter interpreter = new MplInterpreter(programFile);
    new ParseTreeWalker().walk(interpreter, ctx);
    return interpreter;
  }

  private static FileContext parse(File programFile) throws IOException {
    byte[] bytes = Files.readAllBytes(programFile.toPath());
    ANTLRInputStream input =
        new ANTLRInputStream(new String(bytes).replace("\r\n", "\n").replace("\r", "\n"));
    MplLexer lexer = new MplLexer(input);
    TokenStream tokens = new CommonTokenStream(lexer);
    MplParser parser = new MplParser(tokens);
    FileContext context = parser.file();
    return context;
  }

  private final File programFile;
  private final List<String> lines;
  private final List<CompilerException> exceptions = new LinkedList<CompilerException>();
  private final List<CommandChain> chains = new ArrayList<CommandChain>();
  private final List<Command> installation = new ArrayList<Command>();
  private final List<Command> uninstallation = new ArrayList<Command>();
  private final Map<String, List<Include>> includes = new HashMap<String, List<Include>>();
  private final Set<File> imports = new HashSet<File>();

  private MplInterpreter(File programFile) throws IOException {
    this.programFile = programFile;
    lines = Files.readAllLines(programFile.toPath());
    // FIXME was sinnvolleres als null reinstecken
    addFileImport(null, programFile.getParentFile());
  }

  public File getProgramFile() {
    return programFile;
  }

  private boolean project;

  public boolean isProject() {
    return project;
  }

  public List<CompilerException> getExceptions() {
    return exceptions;
  }

  public List<CommandChain> getChains() {
    return chains;
  }

  public List<Command> getInstallation() {
    return installation;
  }

  public List<Command> getUninstallation() {
    return uninstallation;
  }

  /**
   * Returns the mapping of process names to includes required by that process. A key of null
   * indicates that this include is not required by a specific process, but by an explicit include
   * of a project.
   *
   * @return
   */
  public Map<String, List<Include>> getIncludes() {
    return includes;
  }

  @Override
  public void visitErrorNode(ErrorNode node) {
    Token token = node.getSymbol();
    String line = lines.get(token.getLine() - 1);
    exceptions.add(new CompilerException(programFile, token, line, "Mismatched input"));
  }

  @Override
  public void enterInclude(IncludeContext ctx) {
    String includePath = MplLexerUtils.getContainedString(ctx.STRING());
    Token token = ctx.STRING().getSymbol();
    String line = lines.get(token.getLine() - 1);
    File file = new File(programFile.getParentFile(), includePath);
    LinkedList<File> files = new LinkedList<File>();
    if (!addFile(files, file, token)) {
      return;
    }
    Include include = new Include(programFile, token, line, files);
    List<Include> list = includes.get(null);
    if (list == null) {
      list = new LinkedList<Include>();
      includes.put(null, list);
    }
    list.add(include);
  }

  @Override
  public void enterImportDeclaration(ImportDeclarationContext ctx) {
    TerminalNode string = ctx.STRING();
    String importPath = MplLexerUtils.getContainedString(string);
    File file = new File(programFile.getParentFile(), importPath);
    addFileImport(ctx, file);
  }

  /**
   * Adds the file to the list of imports that will be used to search processes. If the file is a
   * directory all direct subfiles will be added, this is not recursive.
   *
   * @param token the token that this import originated from
   * @param file the file to import
   */
  private void addFileImport(ImportDeclarationContext ctx, File file) {
    Token token = ctx != null ? ctx.STRING().getSymbol() : null;
    String line = token != null ? lines.get(token.getLine() - 1) : null;
    if (imports.contains(file)) {
      exceptions.add(new CompilerException(programFile, token, line, "Duplicate import."));
      return;
    }
    addFile(imports, file, token);
  }

  /**
   * Adds the File to the specified Collection. If the File is a Directory all relevant children are
   * added instead. If any Problem occurs, an Exception will be added to the exceptions-List.
   *
   * @param files the Collection to add to
   * @param file the File
   * @param token the Token to display in potential Exceptions
   * @return true if something was added to the Collection, false otherwise
   */
  private boolean addFile(Collection<File> files, File file, Token token) {
    String line = token != null ? lines.get(token.getLine() - 1) : null;
    if (file.isFile()) {
      files.add(file);
      return true;
    } else if (file.isDirectory()) {
      boolean added = false;
      for (File f : file.listFiles()) {
        if (f.isFile() && (f.equals(programFile) || f.getName().endsWith(".mpl"))) {
          files.add(f);
          added = true;
        }
      }
      return added;
    } else if (!file.exists()) {
      String path;
      try {
        path = file.getCanonicalPath();
      } catch (IOException ex) {
        path = file.getAbsolutePath();
      }
      exceptions
          .add(new CompilerException(programFile, token, line, "Could not find '" + path + "'"));
      return false;
    } else {
      exceptions.add(new CompilerException(programFile, token, line,
          "Can only import Files and Directories, not: '" + file + "'"));
      return false;
    }
  }

  private ChainBuffer chainBuffer;

  @Override
  public void enterInstall(InstallContext ctx) {
    chainBuffer = new ChainBuffer();
  }

  @Override
  public void exitInstall(InstallContext ctx) {
    installation.addAll(chainBuffer.getCommands());
    chainBuffer = null;
  }

  @Override
  public void enterUninstall(UninstallContext ctx) {
    chainBuffer = new ChainBuffer();
  }

  @Override
  public void exitUninstall(UninstallContext ctx) {
    uninstallation.addAll(chainBuffer.getCommands());
    chainBuffer = null;
  }

  @Override
  public void enterProject(ProjectContext ctx) {
    project = true;
  }

  private Map<String, Token> ambigiousProcessMapping = new HashMap<String, Token>();

  @Override
  public void enterProcess(ProcessContext ctx) {
    chainBuffer = new ChainBuffer();
    chainBuffer.setProcess(true);
    String name = ctx.IDENTIFIER().getText();
    Token oldToken = ambigiousProcessMapping.get(name);
    Token newToken = ctx.IDENTIFIER().getSymbol();
    if (oldToken != null) {
      String message = "Process " + name + " is ambigious. Every process must have a unique name.";
      CompilerException ex1 =
          new CompilerException(programFile, oldToken, lines.get(oldToken.getLine()), message);
      exceptions.add(ex1);
      CompilerException ex2 =
          new CompilerException(programFile, newToken, lines.get(newToken.getLine()), message);
      exceptions.add(ex2);
    }
    ambigiousProcessMapping.put(name, newToken);
    chainBuffer.setName(name);
    if (ctx.REPEAT() != null) {
      chainBuffer.setRepeatingProcess(true);
      chainBuffer.setRepeatingContext(true);
    } else {
      chainBuffer.setRepeatingProcess(false);
      chainBuffer.setRepeatingContext(false);
    }
  }

  @Override
  public void enterSkriptFile(SkriptFileContext ctx) {
    chainBuffer = new ChainBuffer();
    chainBuffer.setScript(true);
  }

  private CommandBuffer commandBuffer;

  @Override
  public void enterCommandDeclaration(CommandDeclarationContext ctx) {
    commandBuffer = new CommandBuffer();
  }

  @Override
  public void enterModus(ModusContext ctx) {
    Mode mode = Mode.valueOf(ctx.getText().toUpperCase());
    commandBuffer.setMode(mode);
  }

  @Override
  public void enterConditional(ConditionalContext ctx) {
    Conditional conditional = null;
    if (ctx.UNCONDITIONAL() != null) {
      conditional = Conditional.UNCONDITIONAL;
    } else if (ctx.CONDITIONAL() != null) {
      conditional = Conditional.CONDITIONAL;
    } else if (ctx.INVERT() != null) {
      conditional = Conditional.INVERT;
    }
    commandBuffer.setConditional(conditional);
  }

  @Override
  public void enterAuto(AutoContext ctx) {
    Boolean needsRedstone = null;
    if (ctx.ALWAYS_ACTIVE() != null) {
      needsRedstone = false;
    } else if (ctx.NEEDS_REDSTONE() != null) {
      needsRedstone = true;
    }
    commandBuffer.setNeedsRedstone(needsRedstone);
  }

  @Override
  public void enterCommand(CommandContext ctx) {
    String command = ctx.COMMAND().getText();
    commandBuffer.setCommand(command);
  }

  private String lastStartIdentifier;

  @Override
  public void enterStart(StartContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String process = identifier.getText();
    String command = "/execute @e[name=" + process + "] ~ ~ ~ /setblock ~ ~ ~ redstone_block";
    commandBuffer.setCommand(command);
    lastStartIdentifier = process;
    String srcProcess = chainBuffer.isProcess() ? chainBuffer.getName() : null;
    Token symbol = identifier.getSymbol();
    String line = lines.get(symbol.getLine() - 1);
    Include include = new Include(programFile, symbol, process, line, imports);
    List<Include> list = includes.get(srcProcess);
    if (list == null) {
      list = new LinkedList<Include>();
      includes.put(srcProcess, list);
    }
    list.add(include);
  }

  @Override
  public void enterStop(StopContext ctx) {
    String process;
    if (ctx.IDENTIFIER() != null) {
      process = ctx.IDENTIFIER().getText();
    } else if (chainBuffer.isRepeatingProcess()) {
      process = chainBuffer.getName();
    } else {
      Token symbol = ctx.STOP().getSymbol();
      String line = lines.get(symbol.getLine() - 1);
      exceptions.add(
          new CompilerException(programFile, symbol, line, "Can only stop repeating processes."));
      return;
    }
    String command = "/execute @e[name=" + process + "] ~ ~ ~ /setblock ~ ~ ~ stone";
    commandBuffer.setCommand(command);
  }

  @Override
  public void enterWaitfor(WaitforContext ctx) {
    Token symbol = ctx.WAITFOR().getSymbol();
    String line = lines.get(symbol.getLine() - 1);
    if (chainBuffer.isRepeatingContext()) {
      exceptions.add(new CompilerException(programFile, symbol, line,
          "Encountered waitfor in repeating context."));
      return;
    }
    TerminalNode identifier = ctx.IDENTIFIER();
    String process;
    if (identifier != null) {
      process = identifier.getText();
    } else if (lastStartIdentifier != null) {
      process = lastStartIdentifier;
      lastStartIdentifier = null;
    } else {
      exceptions.add(new CompilerException(programFile, symbol, line,
          "Missing Identifier. No previous start was found to wait for."));
      return;
    }
    Boolean conditional = commandBuffer.isConditional();
    if (conditional == null) {
      conditional = false;
    }
    if (conditional) {
      commandBuffer.setCommand("/summon ArmorStand ${this + 3} {CustomName:\"" + process + NOTIFY
          + "\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
      chainBuffer.add(commandBuffer.toCommand());
      chainBuffer.add(new Command("/blockdata ${this - 1} {SuccessCount:1}"));
      chainBuffer.add(new Command("/setblock ${this + 1} redstone_block", true));
      chainBuffer.add(null);
      chainBuffer.add(new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
    } else {
      commandBuffer.setCommand("/summon ArmorStand ${this + 1} {CustomName:\"" + process + NOTIFY
          + "\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
      chainBuffer.add(commandBuffer.toCommand());
      chainBuffer.add(null);
      chainBuffer.add(new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
    }
    commandBuffer = null;
  }

  @Override
  public void enterNotifyDeclaration(NotifyDeclarationContext ctx) {
    if (!chainBuffer.isProcess()) {
      Token symbol = ctx.NOTIFY().getSymbol();
      String line = lines.get(symbol.getLine() - 1);
      exceptions.add(new CompilerException(programFile, symbol, line,
          "Encountered notify outside of a process context."));
      return;
    }
    String method = chainBuffer.getName();
    commandBuffer.setCommand(
        "/execute @e[name=" + method + NOTIFY + "] ~ ~ ~ /setblock ~ ~ ~ redstone_block");
    chainBuffer.add(commandBuffer.toCommand());
    commandBuffer.setCommand("/kill @e[name=" + method + NOTIFY + "]");
  }

  @Override
  public void enterIntercept(InterceptContext ctx) {
    Token symbol = ctx.INTERCEPT().getSymbol();
    String line = lines.get(symbol.getLine() - 1);
    if (chainBuffer.isRepeatingContext()) {
      exceptions.add(new CompilerException(programFile, symbol, line,
          "Encountered intercept in repeating context."));
      return;
    }
    TerminalNode identifier = ctx.IDENTIFIER();
    String process = identifier.getText();
    Boolean conditional = commandBuffer.isConditional();
    if (conditional == null) {
      conditional = false;
    }
    if (conditional) {
      if (commandBuffer.getConditional() == Conditional.CONDITIONAL) {
        chainBuffer.add(new Command("/blockdata ${this - 1} {SuccessCount:0}", true));
        chainBuffer.add(new Command("/blockdata ${this - 1} {SuccessCount:1}"));
      }
      chainBuffer.add(new Command("/setblock ${this + 3} redstone_block", true));
    }
    chainBuffer.add(new Command(
        "/entitydata @e[name=" + process + "] {CustomName:\"" + process + "_INTERCEPTED\"}"));
    chainBuffer.add(new Command("/summon ArmorStand ${this + 1} {CustomName:\"" + process
        + "\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
    chainBuffer.add(null);
    chainBuffer.add(new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
    chainBuffer.add(new Command("/kill @e[name=" + process + ",r=2]"));
    chainBuffer.add(new Command(
        "/entitydata @e[name=" + process + "_INTERCEPTED] {CustomName:\"" + process + "\"}"));
    commandBuffer = null;
  }

  @Override
  public void exitCommandDeclaration(CommandDeclarationContext ctx) {
    if (commandBuffer == null) {
      return;
    }
    if (commandBuffer.getConditional() == Conditional.INVERT) {
      chainBuffer.add(new Command("/blockdata ${this - 1} {SuccessCount:0}", true));
      chainBuffer.add(new Command("/blockdata ${this - 1} {SuccessCount:1}"));
    }
    Command command = commandBuffer.toCommand();
    chainBuffer.add(command);
    commandBuffer = null;

  }

  @Override
  public void enterSkip(SkipContext ctx) {
    chainBuffer.add(null);
  }

  @Override
  public void exitProcess(ProcessContext ctx) {
    LinkedList<Command> commands = chainBuffer.getCommands();
    if (chainBuffer.isRepeatingProcess()) {
      if (commands.size() > 0) {
        Command first = commands.get(0);
        first.setMode(Mode.REPEAT);
        first.setNeedsRedstone(true);
      }
    } else {
      commands.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
    }
    CommandChain chain = new CommandChain(chainBuffer.getName(), commands);
    chains.add(chain);
    chainBuffer = null;
  }

  @Override
  public void exitSkriptFile(SkriptFileContext ctx) {
    CommandChain chain = new CommandChain(null, chainBuffer.getCommands());
    chains.add(chain);
    chainBuffer = null;
  }

}
