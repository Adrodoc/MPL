/*
 * Minecraft Programming Language (MPL): A language for easy development of command block
 * applications including an IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL.
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
 * Minecraft Programming Language (MPL): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, inklusive einer IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL.
 *
 * MPL ist freie Software: Sie können diese unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.interpretation;

import static de.adrodoc55.minecraft.mpl.ast.ProcessType.INLINE;
import static de.adrodoc55.minecraft.mpl.ast.ProcessType.REMOTE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import de.adrodoc55.minecraft.mpl.antlr.MplParser;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.AutoContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ConditionalContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.FileContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ImportDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.InsertContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.InstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModifiableCommandContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModusContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplBreakContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplBreakpointContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplCallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplContinueContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplElseContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplIfContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplInterceptContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplNotifyContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplSkipContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplStartContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplStopContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplThenContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplWaitforContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MplWhileContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.OrientationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProcessContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProjectContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ScriptFileContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.UninstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.VariableDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParserBaseListener;
import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.ProcessType;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModifiableChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCall;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreak;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinue;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.ast.variable.Insertable;
import de.adrodoc55.minecraft.mpl.ast.variable.MplVariable;
import de.adrodoc55.minecraft.mpl.ast.variable.type.MplType;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.ChainPartBuffer.ChainPartBufferImpl;
import de.adrodoc55.minecraft.mpl.interpretation.insert.GlobalVariableInsert;
import de.adrodoc55.minecraft.mpl.interpretation.insert.LocalVariableInsert;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;

/**
 * @author Adrodoc55
 */
public class MplInterpreter extends MplParserBaseListener {

  public static MplInterpreter interpret(File programFile, MplCompilerContext context)
      throws IOException {
    MplInterpreter interpreter = new MplInterpreter(programFile, context);
    FileContext ctx = interpreter.parse();
    if (context.getErrors().isEmpty()) {
      new ParseTreeWalker().walk(interpreter, ctx);
    }
    return interpreter;
  }

  private FileContext parse() throws IOException {
    byte[] bytes = Files.readAllBytes(programFile.toPath());
    ANTLRInputStream input = new ANTLRInputStream(FileUtils.toUnixLineEnding(new String(bytes)));
    MplLexer lexer = new MplLexer(input);
    TokenStream tokens = new CommonTokenStream(lexer);
    MplParser parser = new MplParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object token, int line,
          int charPositionInLine, String message, RecognitionException cause) {
        MplSource source = toSource((Token) token);
        context.addError(new CompilerException(source, message));
      }
    });
    FileContext context = parser.file();
    // Trees.inspect(context, parser).get();
    return context;
  }

  private final MplCompilerContext context;
  private final File programFile;
  private final List<String> lines;
  private final SetMultimap<String, MplReference> references = HashMultimap.create();
  private final Set<File> imports = new HashSet<>();

  private MplInterpreter(File programFile, MplCompilerContext context) throws IOException {
    this.context = context;
    program = new MplProgram(programFile, context);
    this.programFile = programFile;
    lines = Files.readAllLines(programFile.toPath());
    addFileImport(null, programFile.getParentFile());
  }

  public MplCompilerContext getContext() {
    return context;
  }

  public File getProgramFile() {
    return programFile;
  }

  private final MplProgram program;

  public MplProgram getProgram() {
    return program;
  }

  /**
   * Returns the read only mapping of process names to the references of each process.
   *
   * @return the references
   */
  public SetMultimap<String, MplReference> getReferences() {
    return Multimaps.unmodifiableSetMultimap(references);
  }

  // ----------------------------------------------------------------------------------------------------

  public MplSource toSource(@Nonnull Token token) {
    String line = token.getLine() > 0 ? lines.get(token.getLine() - 1) : "";
    return new MplSource(programFile, token, line);
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    switch (node.getSymbol().getType()) {
      case MplLexer.COMMAND_STRING:
        visitCommandString(node);
        break;
    }
  }

  @Override
  public void exitFile(FileContext ctx) {
    if (program.getOrientation() == null) {
      program.setOrientation(new Orientation3D());
    }
  }

  @Override
  public void enterImportDeclaration(ImportDeclarationContext ctx) {
    Token token = ctx.STRING().getSymbol();
    String importPath = MplLexerUtils.getContainedString(token);
    File file = new File(programFile.getParentFile(), importPath);
    addFileImport(ctx, file);
  }

  @Override
  public void enterProject(ProjectContext ctx) {
    Token oldToken = program.getToken();
    Token newToken = ctx.PROJECT().getSymbol();
    if (oldToken != null) {
      String message = "A file can only contain a single project";
      MplSource oldSource = toSource(oldToken);
      context.addError(new CompilerException(oldSource, message));
      MplSource newSource = toSource(newToken);
      context.addError(new CompilerException(newSource, message));
      return;
    }
    String name = ctx.IDENTIFIER().getText();
    program.setName(name);
    program.setToken(newToken);
  }

  @Override
  public void enterOrientation(OrientationContext ctx) {
    String def = MplLexerUtils.getContainedString(ctx.STRING().getSymbol());

    Token newToken = ctx.ORIENTATION().getSymbol();

    Orientation3D oldOrientation = program.getOrientation();
    if (oldOrientation != null) {
      String type = program.isScript() ? "script" : "project";
      String message = "A " + type + " can only have a single orientation";
      Token oldToken = oldOrientation.getToken();
      MplSource oldSource = toSource(oldToken);
      context.addError(new CompilerException(oldSource, message));
      MplSource newSource = toSource(newToken);
      context.addError(new CompilerException(newSource, message));
      return;
    }

    Orientation3D newOrientation = new Orientation3D(def, newToken);
    program.setOrientation(newOrientation);
  }

  private final Set<File> included = new HashSet<>();

  @Override
  public void enterInclude(IncludeContext ctx) {
    String includePath = MplLexerUtils.getContainedString(ctx.STRING().getSymbol());
    Token token = ctx.STRING().getSymbol();
    File includeFile = new File(programFile.getParentFile(), includePath);
    MplSource source = toSource(token);
    if (!included.add(includeFile)) {
      context.addError(new CompilerException(source, "Duplicate include"));
    }
    List<File> files = new ArrayList<File>();
    if (!addFile(files, includeFile, token)) {
      return;
    }
    for (File file : files) {
      context.addInclude(new MplInclude(file, source));
    }
  }

  /**
   * Adds the file to the list of imports that will be used to search processes. If the file is a
   * directory all direct subfiles will be added, this is not recursive.
   *
   * @param ctx the token context that this import originated from
   * @param file the file to import
   */
  private void addFileImport(ImportDeclarationContext ctx, File file) {
    Token token = ctx != null ? ctx.STRING().getSymbol() : null;
    if (imports.contains(file)) {
      MplSource source = toSource(token);
      context.addError(new CompilerException(source, "Duplicate import"));
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
   * @param token the Token to display potential Exceptions
   * @return true if something was added to the Collection, false otherwise
   */
  private boolean addFile(Collection<File> files, File file, Token token) {
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
      String path = FileUtils.getCanonicalPath(file);
      MplSource source = toSource(token);
      context.addError(new CompilerException(source, "Could not find '" + path + "'"));
      return false;
    } else {
      MplSource source = toSource(token);
      context.addError(new CompilerException(source,
          "Can only import Files and Directories, not: '" + file + "'"));
      return false;
    }
  }

  private final Deque<ChainPartBuffer> chainBufferStack = new LinkedList<>();
  private ChainPartBuffer chainBuffer;

  private void newChainBuffer() {
    chainBufferStack.push(chainBuffer);
    chainBuffer = new ChainPartBufferImpl();
  }

  private void popChainBuffer() {
    chainBuffer = chainBufferStack.poll();
  }

  private MplProcess process;

  @Override
  public void enterInstall(InstallContext ctx) {
    newChainBuffer();
  }

  @Override
  public void exitInstall(InstallContext ctx) {
    MplProcess install = program.getInstall();
    if (install == null) {
      install = new MplProcess("install", toSource(ctx.INSTALL().getSymbol()));
      program.setInstall(install);
    }
    install.addAll(chainBuffer.getChainParts());

    popChainBuffer();
  }

  @Override
  public void enterUninstall(UninstallContext ctx) {
    newChainBuffer();
  }

  @Override
  public void exitUninstall(UninstallContext ctx) {
    MplProcess install = program.getUninstall();
    if (install == null) {
      install = new MplProcess("uninstall", toSource(ctx.UNINSTALL().getSymbol()));
      program.setUninstall(install);
    }
    install.addAll(chainBuffer.getChainParts());

    popChainBuffer();
  }

  @Override
  public void enterScriptFile(ScriptFileContext ctx) {
    program.setScript(true);
    newChainBuffer();
  }

  @Override
  public void exitScriptFile(ScriptFileContext ctx) {
    process = new MplProcess(toSource(new CommonToken(MplLexer.PROCESS)));
    process.setChainParts(chainBuffer.getChainParts());
    program.addProcess(process);
    process = null;

    popChainBuffer();
  }

  @Override
  public void enterProcess(ProcessContext ctx) {
    String name = ctx.IDENTIFIER().getText();
    boolean repeat = ctx.REPEAT() != null;
    ProcessType type = ProcessType.DEFAULT;
    if (ctx.INLINE() != null) {
      type = INLINE;
    } else if (ctx.REMOTE() != null || ctx.IMPULSE() != null || repeat) {
      type = REMOTE;
    }
    if (type == INLINE && (ctx.IMPULSE() != null || repeat)) {
      TerminalNode terminal = repeat ? ctx.REPEAT() : ctx.IMPULSE();
      context.addError(new CompilerException(toSource(terminal.getSymbol()),
          "Illegal combination of modifiers for the process " + name
              + "; only one of inline, impulse, or repeat is permitted"));
      repeat = false;
    }
    Collection<String> tags = new ArrayList<>(ctx.TAG().size());
    for (TerminalNode tag : ctx.TAG()) {
      tags.add(MplLexerUtils.getTagString(tag.getSymbol()));
    }
    MplSource source = toSource(ctx.IDENTIFIER().getSymbol());
    process = new MplProcess(name, repeat, type, tags, source);
    newChainBuffer();
  }

  @Override
  public void exitProcess(ProcessContext ctx) {
    process.setChainParts(chainBuffer.getChainParts());
    program.addProcess(process);
    process = null;

    popChainBuffer();
  }

  private ModifierBuffer modifierBuffer;

  @Override
  public void enterModifiableCommand(ModifiableCommandContext ctx) {
    modifierBuffer = new ModifierBuffer();
  }

  @Override
  public void exitModifiableCommand(ModifiableCommandContext ctx) {
    modifierBuffer = null;
  }

  @Override
  public void enterModus(ModusContext ctx) {
    if (ctx.IMPULSE() != null) {
      modifierBuffer.setModeToken(ctx.IMPULSE().getSymbol());
      modifierBuffer.setMode(Mode.IMPULSE);
      return;
    }
    if (ctx.CHAIN() != null) {
      modifierBuffer.setModeToken(ctx.CHAIN().getSymbol());
      modifierBuffer.setMode(Mode.CHAIN);
      return;
    }
    if (ctx.REPEAT() != null) {
      modifierBuffer.setModeToken(ctx.REPEAT().getSymbol());
      modifierBuffer.setMode(Mode.REPEAT);
      return;
    }
  }

  @Override
  public void enterConditional(ConditionalContext ctx) {
    if (ctx.UNCONDITIONAL() != null) {
      modifierBuffer.setConditionalToken(ctx.UNCONDITIONAL().getSymbol());
      modifierBuffer.setConditional(Conditional.UNCONDITIONAL);
      return;
    }
    if (ctx.CONDITIONAL() != null) {
      modifierBuffer.setConditionalToken(ctx.CONDITIONAL().getSymbol());
      modifierBuffer.setConditional(Conditional.CONDITIONAL);
      return;
    }
    if (ctx.INVERT() != null) {
      modifierBuffer.setConditionalToken(ctx.INVERT().getSymbol());
      modifierBuffer.setConditional(Conditional.INVERT);
      return;
    }
  }

  @Override
  public void enterAuto(AutoContext ctx) {
    if (ctx.ALWAYS_ACTIVE() != null) {
      modifierBuffer.setNeedsRedstoneToken(ctx.ALWAYS_ACTIVE().getSymbol());
      modifierBuffer.setNeedsRedstone(false);
      return;
    }
    if (ctx.NEEDS_REDSTONE() != null) {
      modifierBuffer.setNeedsRedstoneToken(ctx.NEEDS_REDSTONE().getSymbol());
      modifierBuffer.setNeedsRedstone(true);
      return;
    }
  }

  /**
   * Check that the given {@link Token} is null. If it is not null add a {@link CompilerException}.
   *
   * @param part - name of the {@link ChainPart} that may not have this modifier
   * @param token to check
   */
  private void checkNoModifier(String part, Token token) {
    if (token == null) {
      return;
    }
    MplSource source = toSource(token);
    context.addError(new CompilerException(source, "Illegal modifier for " + part
        + "; only unconditional, conditional and invert are permitted"));
  }

  private void addModifiableChainPart(ModifiableChainPart chainPart) {
    Conditional conditional = chainPart.getConditional();
    if (conditional == Conditional.UNCONDITIONAL) {
      chainBuffer.add(chainPart);
      return;
    }
    ChainPart prev = chainBuffer.getChainParts().peekLast();
    if (prev == null) {
      Token token = modifierBuffer.getConditionalToken();
      MplSource source = toSource(token);
      context.addError(
          new CompilerException(source, "The first part of a chain must be unconditional"));
      return;
    }
    if (prev.canBeDependedOn()) {
      chainPart.setPrevious(prev);
      chainBuffer.add(chainPart);
    } else {
      Token token = modifierBuffer.getConditionalToken();
      MplSource source = toSource(token);
      context.addError(new CompilerException(source,
          conditional.name().toLowerCase() + " cannot depend on " + prev.getName()));
    }
  }

  private List<Object> commandParts;

  @Override
  public void enterCommand(CommandContext ctx) {
    commandParts = new ArrayList<>();
  }

  @Override
  public void enterInsert(InsertContext ctx) {
    TerminalNode identifierNode = ctx.IDENTIFIER();
    String identifier = identifierNode.getText();
    TerminalNode integer = ctx.UNSIGNED_INTEGER();
    if (integer != null) {
      commandParts.add(new RelativeThisInsert(Integer.parseInt(integer.getText())));
      return;
    } // TODO: RelativeOriginInsert

    MplVariable<?> variable = variableScope.findVariable(identifier);
    if (variable != null) {
      if (variable instanceof Insertable) {
        commandParts.add(new LocalVariableInsert((Insertable) variable));
      } else {
        context.addError(new CompilerException(toSource(identifierNode.getSymbol()), ""));
      }
    } else {
      commandParts.add(new GlobalVariableInsert(identifier, imports));
    }
  }

  private void visitCommandString(TerminalNode node) {

  }

  @Override
  public void exitCommand(CommandContext ctx) {
    // FIXME Use whole context instead of token
    Token token = ctx.SLASH().getSymbol();
    MplCommand command = new MplCommand(commandParts, modifierBuffer, toSource(token));
    addModifiableChainPart(command);
    commandParts = null;
  }

  @Override
  public void enterMplCall(MplCallContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String process = identifier.getText();
    MplCall call = new MplCall(process, toSource(identifier.getSymbol()));
    chainBuffer.add(call);

    if (program.isScript()) {
      return;
    }
    String srcProcess = this.process != null ? this.process.getName() : null;
    Token token = identifier.getSymbol();
    MplSource source = toSource(token);
    references.put(srcProcess, new MplProcessReference(process, imports, source));
  }

  private String toSelector(String text) {
    return "@e[name=" + text + "]";
  }

  private String lastStartIdentifier;

  @Override
  public void enterMplStart(MplStartContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String selector;
    MplSource source;
    if (identifier != null) {
      selector = toSelector(identifier.getText());
      source = toSource(identifier.getSymbol());
    } else {
      selector = ctx.SELECTOR().getText();
      source = toSource(ctx.SELECTOR().getSymbol());
    }
    MplStart start = new MplStart(selector, modifierBuffer, source);
    addModifiableChainPart(start);

    checkNoModifier(start.getName(), modifierBuffer.getModeToken());
    checkNoModifier(start.getName(), modifierBuffer.getNeedsRedstoneToken());

    if (identifier != null) {
      String process = identifier.getText();
      lastStartIdentifier = process;
      if (program.isScript()) {
        return;
      }

      String srcProcess = this.process != null ? this.process.getName() : null;
      references.put(srcProcess, new MplProcessReference(process, imports, source));
    }
  }

  @Override
  public void enterMplStop(MplStopContext ctx) {
    Token token = ctx.STOP().getSymbol();
    MplSource source = toSource(token);

    TerminalNode identifier = ctx.IDENTIFIER();

    String selector;
    if (ctx.SELECTOR() != null) {
      selector = ctx.SELECTOR().getText();
      source = toSource(ctx.SELECTOR().getSymbol());
    } else if (identifier != null) {
      selector = toSelector(identifier.getText());
      source = toSource(identifier.getSymbol());
    } else if (this.process != null) {
      if (this.process.isRepeating()) {
        selector = toSelector(this.process.getName());
      } else {
        context.addError(new CompilerException(source, "An impulse process cannot be stopped"));
        return;
      }
    } else {
      context.addError(new CompilerException(source, "Missing identifier"));
      return;
    }

    MplStop stop = new MplStop(selector, modifierBuffer, source);
    addModifiableChainPart(stop);

    checkNoModifier(stop.getName(), modifierBuffer.getModeToken());
    checkNoModifier(stop.getName(), modifierBuffer.getNeedsRedstoneToken());
  }

  @Override
  public void enterMplWaitfor(MplWaitforContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String event;
    MplSource source = toSource(ctx.WAITFOR().getSymbol());
    if (identifier != null) {
      event = identifier.getText();
      source = toSource(identifier.getSymbol());
    } else if (lastStartIdentifier != null) {
      event = lastStartIdentifier;
      lastStartIdentifier = null;
    } else {
      context.addError(new CompilerException(source,
          "Missing identifier; no previous start was found to wait for"));
      return;
    }
    MplWaitfor waitfor = new MplWaitfor(event, modifierBuffer, source);
    addModifiableChainPart(waitfor);

    checkNoModifier(waitfor.getName(), modifierBuffer.getModeToken());
    checkNoModifier(waitfor.getName(), modifierBuffer.getNeedsRedstoneToken());
  }

  @Override
  public void enterMplNotify(MplNotifyContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String event = identifier.getText();
    MplSource source = toSource(identifier.getSymbol());
    MplNotify notify = new MplNotify(event, modifierBuffer, source);
    addModifiableChainPart(notify);

    checkNoModifier(notify.getName(), modifierBuffer.getModeToken());
    checkNoModifier(notify.getName(), modifierBuffer.getNeedsRedstoneToken());
  }

  @Override
  public void enterMplIntercept(MplInterceptContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String process = identifier.getText();
    MplSource source = toSource(identifier.getSymbol());
    MplIntercept intercept = new MplIntercept(process, modifierBuffer, source);
    addModifiableChainPart(intercept);

    checkNoModifier(intercept.getName(), modifierBuffer.getModeToken());
    checkNoModifier(intercept.getName(), modifierBuffer.getNeedsRedstoneToken());
  }

  @Override
  public void enterMplBreakpoint(MplBreakpointContext ctx) {
    int line = ctx.BREAKPOINT().getSymbol().getLine();
    String message = programFile.getName() + " : line " + line;
    MplBreakpoint breakpoint =
        new MplBreakpoint(message, modifierBuffer, toSource(ctx.BREAKPOINT().getSymbol()));
    addModifiableChainPart(breakpoint);

    checkNoModifier(breakpoint.getName(), modifierBuffer.getModeToken());
    checkNoModifier(breakpoint.getName(), modifierBuffer.getNeedsRedstoneToken());
  }

  @Override
  public void enterMplSkip(MplSkipContext ctx) {
    if (process != null && process.isRepeating() && chainBuffer.getChainParts().isEmpty()) {
      MplSource source = toSource(ctx.SKIP_TOKEN().getSymbol());
      context.addError(
          new CompilerException(source, "skip cannot be the first command of a repeating process"));
      return;
    }
    chainBuffer.add(new MplSkip());
  }

  @Override
  public void enterMplIf(MplIfContext ctx) {
    boolean not = ctx.NOT() != null;
    // FIXME: MplIf needs to support any dependable command as condition
    String condition = ctx.command().getText();
    chainBuffer = new MplIf(chainBuffer, not, condition, toSource(ctx.IF().getSymbol()));
  }

  @Override
  public void enterMplThen(MplThenContext ctx) {
    ((MplIf) chainBuffer).enterThen();
  }

  @Override
  public void enterMplElse(MplElseContext ctx) {
    ((MplIf) chainBuffer).enterElse();
  }

  @Override
  public void exitMplIf(MplIfContext ctx) {
    MplIf mplIf = (MplIf) chainBuffer;
    chainBuffer = mplIf.exit();
    chainBuffer.add(mplIf);
  }

  private Deque<MplWhile> loops = new ArrayDeque<>();

  @Override
  public void enterMplWhile(MplWhileContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String label = identifier != null ? identifier.getText() : null;
    boolean not = ctx.NOT() != null;
    boolean trailing = ctx.DO() != null;
    // FIXME: MplWhile needs to support any dependable command as condition
    CommandContext command = ctx.command();
    String condition = command != null ? command.getText() : null;

    MplWhile mplWhile = new MplWhile(chainBuffer, label, not, trailing, condition,
        toSource(ctx.WHILE() != null ? ctx.WHILE().getSymbol() : ctx.REPEAT().getSymbol()));
    loops.push(mplWhile);
    chainBuffer = mplWhile;
  }

  @Override
  public void exitMplWhile(MplWhileContext ctx) {
    loops.pop();
    MplWhile mplWhile = (MplWhile) chainBuffer;
    chainBuffer = mplWhile.exit();
    chainBuffer.add(mplWhile);
  }

  @Override
  public void enterMplBreak(MplBreakContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String label = identifier != null ? identifier.getText() : null;

    MplWhile loop;
    if (label == null) {
      MplSource source = toSource(ctx.BREAK().getSymbol());
      loop = findParentLoop(source);
    } else {
      MplSource source = toSource(ctx.IDENTIFIER().getSymbol());
      loop = findParentLoop(label, source);
    }
    if (loop == null) {
      return;
    }

    MplBreak mplBreak =
        new MplBreak(label, loop, modifierBuffer, toSource(ctx.BREAK().getSymbol()));
    addModifiableChainPart(mplBreak);

    checkNoModifier(mplBreak.getName(), modifierBuffer.getModeToken());
    checkNoModifier(mplBreak.getName(), modifierBuffer.getNeedsRedstoneToken());
  }

  @Override
  public void enterMplContinue(MplContinueContext ctx) {
    TerminalNode identifier = ctx.IDENTIFIER();
    String label = identifier != null ? identifier.getText() : null;

    MplWhile loop;
    if (label == null) {
      MplSource source = toSource(ctx.CONTINUE().getSymbol());
      loop = findParentLoop(source);
    } else {
      MplSource source = toSource(ctx.IDENTIFIER().getSymbol());
      loop = findParentLoop(label, source);
    }
    if (loop == null) {
      return;
    }

    MplContinue mplContinue =
        new MplContinue(label, loop, modifierBuffer, toSource(ctx.CONTINUE().getSymbol()));
    addModifiableChainPart(mplContinue);

    checkNoModifier(mplContinue.getName(), modifierBuffer.getModeToken());
    checkNoModifier(mplContinue.getName(), modifierBuffer.getNeedsRedstoneToken());
  }

  public MplWhile findParentLoop(MplSource source) {
    MplWhile loop;
    loop = loops.peek();
    if (loop == null) {
      context.addError(
          new CompilerException(source, source.token.getText() + " can only be used in a loop"));
    }
    return loop;
  }

  public MplWhile findParentLoop(String label, MplSource source) {
    MplWhile loop = null;
    for (MplWhile mplWhile : loops) {
      if (label.equals(mplWhile.getLabel())) {
        loop = mplWhile;
        break;
      }
    }
    if (loop == null) {
      context.addError(new CompilerException(source, "Missing label " + label));
    }
    return loop;
  }

  private VariableScope variableScope = new VariableScope(null);

  @Override
  public void enterVariableDeclaration(VariableDeclarationContext ctx) {
    MplType<?> declaredType = MplType.valueOf(ctx.TYPE().getText().toUpperCase(Locale.ENGLISH));
    List<TerminalNode> identifiers = ctx.IDENTIFIER();
    TerminalNode identifier = identifiers.get(0);
    TerminalNode string = ctx.STRING();
    TerminalNode integer = ctx.INTEGER();
    TerminalNode selector = ctx.SELECTOR();
    TerminalNode scoreboard = identifiers.size() > 1 ? identifiers.get(1) : null;
    MplType<?> actualType;
    MplSource actualSource;
    String value;
    if (string != null) {
      actualType = MplType.STRING;
      actualSource = toSource(string.getSymbol());
      value = MplLexerUtils.getContainedString(string.getSymbol());
    } else if (integer != null) {
      actualType = MplType.INTEGER;
      actualSource = toSource(integer.getSymbol());
      value = integer.getText();
    } else if (scoreboard != null) {
      actualType = MplType.VALUE;
      actualSource = toSource(scoreboard.getSymbol());
      value = selector.getText() + " " + scoreboard.getText();
    } else if (selector != null) {
      actualType = MplType.SELECTOR;
      actualSource = toSource(selector.getSymbol());
      value = selector.getText();
    } else {
      throw new InternalError("Unreachable code");
    }
    if (!declaredType.isAssignableFrom(actualType)) {
      context.addError(new CompilerException(actualSource,
          "Type mismatch: cannot convert from " + actualType + " to " + declaredType));
      return;
    }

    MplSource declarationSource = toSource(identifier.getSymbol());
    MplVariable<?> variable = declaredType.newVariable(declarationSource, identifier.getText());
    variable.setValueString(value, actualSource, context);
    try {
      variableScope.declareVariable(variable);
    } catch (DuplicateVariableException ex) {
      context.addError(new CompilerException(declarationSource,
          "Duplicate local variable " + identifier.getText()));
    }
  }
}
