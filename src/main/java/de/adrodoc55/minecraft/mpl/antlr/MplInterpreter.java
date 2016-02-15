/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
 *
 * MPL is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MPL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MPL. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 *
 *
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
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

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.CommandChain;
import de.adrodoc55.minecraft.mpl.CompilerException;
import de.adrodoc55.minecraft.mpl.antlr.CommandBufferFactory.CommandBuffer;
import de.adrodoc55.minecraft.mpl.antlr.CommandBufferFactory.CommandBuffer.Conditional;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.AutoContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ChainContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ConditionalContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ElseDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.FileContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IfDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ImportDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.InstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.InterceptContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModusContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.NotifyDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProcessContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProjectContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkipContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkriptContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.StartContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.StopContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.UninstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.WaitforContext;
import de.adrodoc55.minecraft.mpl.antlr.commands.InternalCommand;
import de.adrodoc55.minecraft.mpl.antlr.commands.InvertingCommand;

public class MplInterpreter extends MplBaseListener {

  private static final String NOTIFY = "_NOTIFY";

  public static MplInterpreter interpret(File programFile) throws IOException {
    FileContext ctx = parse(programFile);
    ctx.toStringTree();
    MplInterpreter interpreter = new MplInterpreter(programFile);
    new ParseTreeWalker().walk(interpreter, ctx);
    return interpreter;
  }

  private static FileContext parse(File programFile) throws IOException {
    byte[] bytes = Files.readAllBytes(programFile.toPath());
    ANTLRInputStream input = new ANTLRInputStream(FileUtils.toUnixLineEnding(new String(bytes)));
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
  public void enterSkript(SkriptContext ctx) {
    chainBuffer = new ChainBuffer();
    chainBuffer.setScript(true);
  }

  private final LinkedList<CommandBufferFactory> factory = new LinkedList<>();

  @Override
  public void enterChain(ChainContext ctx) {
    factory.push(new CommandBufferFactory());
  }

  @Override
  public void exitChain(ChainContext ctx) {
    factory.pop();
  }

  private CommandBuffer commandBuffer;

  @Override
  public void enterCommandDeclaration(CommandDeclarationContext ctx) {
    commandBuffer = factory.peek().create();
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
    try {
      commandBuffer.setConditional(conditional);
    } catch (IllegalModifierException ex) {
      Token symbol;
      if (ctx.CONDITIONAL() != null) {
        symbol = ctx.CONDITIONAL().getSymbol();
      } else if (ctx.INVERT() != null) {
        symbol = ctx.INVERT().getSymbol();
      } else {
        throw new RuntimeException(
            "IllegalModifierException was thrown, but could not find the conditional Token", ex);
      }
      String line = lines.get(symbol.getLine() - 1);
      exceptions
          .add(new CompilerException(programFile, symbol, line, ex.getLocalizedMessage(), ex));
    }
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
    if (commandBuffer != null) {
      commandBuffer.setCommand(command);
    }
  }

  private String lastStartIdentifier;

  @Override
  public void enterStart(StartContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String process = identifier.getText();
    String command = "execute @e[name=" + process + "] ~ ~ ~ setblock ~ ~ ~ redstone_block";
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
    String command = "execute @e[name=" + process + "] ~ ~ ~ setblock ~ ~ ~ stone";
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
      commandBuffer.setCommand("summon ArmorStand ${this + 3} {CustomName:" + process + NOTIFY
          + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
      chainBuffer.add(commandBuffer.toCommand());
      chainBuffer.add(new Command("blockdata ${this - 1} {SuccessCount:1}"));
      chainBuffer.add(new Command("setblock ${this + 1} redstone_block", true));
      chainBuffer.add(null);
      chainBuffer.add(new Command("setblock ${this - 1} stone", Mode.IMPULSE, false));
    } else {
      commandBuffer.setCommand("summon ArmorStand ${this + 1} {CustomName:" + process + NOTIFY
          + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
      chainBuffer.add(commandBuffer.toCommand());
      chainBuffer.add(null);
      chainBuffer.add(new Command("setblock ${this - 1} stone", Mode.IMPULSE, false));
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
    commandBuffer
        .setCommand("execute @e[name=" + method + NOTIFY + "] ~ ~ ~ setblock ~ ~ ~ redstone_block");
    chainBuffer.add(commandBuffer.toCommand());
    commandBuffer.setCommand("kill @e[name=" + method + NOTIFY + "]");
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
        addInvert(chainBuffer);
      }
      chainBuffer.add(new Command("setblock ${this + 3} redstone_block", true));
    }
    chainBuffer.add(new Command(
        "entitydata @e[name=" + process + "] {CustomName:" + process + "_INTERCEPTED}"));
    chainBuffer.add(new Command("summon ArmorStand ${this + 1} {CustomName:" + process
        + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
    chainBuffer.add(null);
    chainBuffer.add(new Command("setblock ${this - 1} stone", Mode.IMPULSE, false));
    chainBuffer.add(new Command("kill @e[name=" + process + ",r=2]"));
    chainBuffer.add(new Command(
        "entitydata @e[name=" + process + "_INTERCEPTED] {CustomName:" + process + "}"));
    commandBuffer = null;
  }

  @Override
  public void exitCommandDeclaration(CommandDeclarationContext ctx) {
    if (commandBuffer == null) {
      return;
    }
    if (commandBuffer.getConditional() == Conditional.INVERT) {
      addInvert(chainBuffer);
    }
    Command command = commandBuffer.toCommand();
    chainBuffer.add(command);
    commandBuffer = null;
  }

  /**
   * This method add's the inverting command to the given {@link ChainBuffer}.
   *
   * @param chainBuffer
   */
  private static void addInvert(ChainBuffer chainBuffer) {
    LinkedList<Command> commands = chainBuffer.getCommands();
    if (commands.isEmpty()) {
      throw new IllegalArgumentException(
          "The given ChainBuffer is empty. The first command of a chain cannot be an invert command.");
    }
    Command previous = commands.peekLast();
    chainBuffer.add(new InvertingCommand(previous));
  }

  @Override
  public void enterSkip(SkipContext ctx) {
    chainBuffer.add(null);
  }

  private final IfBuffer ifBuffer = new IfBuffer(chainBuffer);

  @Override
  public void enterIfDeclaration(IfDeclarationContext ctx) {
    boolean not = ctx.NOT() != null;
    Command condition = new Command(ctx.command().COMMAND().getText());
    ifBuffer.enterIf(not, condition);



    chainBuffer.add();
    chainBuffer = new IfChainBuffer2(not, chainBuffer);
  }

  @Override
  public void enterElseDeclaration(ElseDeclarationContext ctx) {
    ifBuffer.switchToElseBlock();



    ((IfChainBuffer2) chainBuffer).switchToElseBlock();
  }

  @Override
  public void exitIfDeclaration(IfDeclarationContext ctx) {
    chainBuffer = ifBuffer.exitIf();



    chainBuffer = ((IfChainBuffer2) chainBuffer).getOriginal();
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
      commands.add(0, new InternalCommand("setblock ${this - 1} stone", Mode.IMPULSE, false));
    }
    CommandChain chain = new CommandChain(chainBuffer.getName(), commands);
    chains.add(chain);
    chainBuffer = null;
  }

  @Override
  public void exitSkript(SkriptContext ctx) {
    CommandChain chain = new CommandChain(null, chainBuffer.getCommands());
    chains.add(chain);
    chainBuffer = null;
  }

}
