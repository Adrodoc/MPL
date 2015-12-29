package de.adrodoc55.minecraft.mpl.antlr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import de.adrodoc55.minecraft.mpl.antlr.MplParser.AutoContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ConditionalContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ExecuteContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.InstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MethodContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModusContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProgramContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProjectContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkipContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkriptContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.UninstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.WaitforContext;

public class MplInterpreter extends MplBaseListener {

    private static final String RETURN = "_RETURN";

    public static MplInterpreter interpret(File programFile) throws IOException {
        ProgramContext ctx = parse(programFile);
        MplInterpreter interpreter = new MplInterpreter(programFile);
        new ParseTreeWalker().walk(interpreter, ctx);
        return interpreter;
    }

    private static ProgramContext parse(File programFile) throws IOException {
        BufferedReader reader = Files.newBufferedReader(programFile.toPath());
        ANTLRInputStream input = new ANTLRInputStream(reader);
        MplLexer lexer = new MplLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        MplParser parser = new MplParser(tokens);
        ProgramContext context = parser.program();
        return context;
    }

    private final File programFile;

    private final List<CommandChain> chains = new ArrayList<CommandChain>();
    private final List<Command> installation = new ArrayList<Command>();
    private final List<Command> uninstallation = new ArrayList<Command>();
    private final Set<Include> includes = new HashSet<Include>();

    private MplInterpreter(File programFile) {
        this.programFile = programFile;
    }

    /**
     * Returns the name of the current Project, Method or Skript. The Name is
     * defined by the Name of the programFile.
     *
     * @return name
     */
    private String getName() {
        return FileUtils.getFilenameWithoutExtension(programFile);
    }

    public File getProgramFile() {
        return programFile;
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

    public Set<Include> getIncludes() {
        return includes;
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        Token token = node.getSymbol();
        throw new GrammarException("Mismatched input: '" + node.getText()
                + "' in File '" + this.getProgramFile() + "' in Line "
                + token.getLine() + " in Column " + token.getStartIndex());
    }

    @Override
    public void enterInclude(IncludeContext ctx) {
        this.includes.add(new Include(this.programFile, this.programFile
                .getParentFile(), ctx));
    }

    private LinkedList<Command> commands;

    @Override
    public void enterInstall(InstallContext ctx) {
        this.commands = new LinkedList<Command>();
    }

    @Override
    public void exitInstall(InstallContext ctx) {
        this.installation.addAll(this.commands);
        this.commands = null;
    }

    @Override
    public void enterUninstall(UninstallContext ctx) {
        this.commands = new LinkedList<Command>();
    }

    @Override
    public void exitUninstall(UninstallContext ctx) {
        this.uninstallation.addAll(this.commands);
        this.commands = null;
    }

    private boolean project = false;

    public boolean isProject() {
        return project;
    }

    @Override
    public void enterProject(ProjectContext ctx) {
        project = true;
    }

    @Override
    public void enterMethod(MethodContext ctx) {
        this.commands = new LinkedList<Command>();
        this.commands.add(new Command("/setblock ${this - 1} stone",
                Mode.IMPULSE, false));
    }

    @Override
    public void enterSkript(SkriptContext ctx) {
        this.commands = new LinkedList<Command>();
    }

    private CommandBuffer commandBuffer;

    @Override
    public void enterCommandDeclaration(CommandDeclarationContext ctx) {
        this.commandBuffer = new CommandBuffer();
    }

    @Override
    public void enterModus(ModusContext ctx) {
        Mode mode = Mode.valueOf(ctx.getText().toUpperCase());
        this.commandBuffer.setMode(mode);
    }

    @Override
    public void enterConditional(ConditionalContext ctx) {
        Boolean conditional = null;
        if (ctx.UNCONDITIONAL() != null) {
            conditional = false;
        } else if (ctx.CONDITIONAL() != null) {
            conditional = true;
        } else if (ctx.INVERT() != null) {
            commands.add(new Command("/blockdata ${this - 1} {SuccessCount:0}",
                    true));
            commands.add(new Command("/blockdata ${this - 1} {SuccessCount:1}"));
            conditional = true;
        }
        this.commandBuffer.setConditional(conditional);
    }

    @Override
    public void enterAuto(AutoContext ctx) {
        Boolean needsRedstone = null;
        if (ctx.ALWAYS_ACTIVE() != null) {
            needsRedstone = false;
        } else if (ctx.NEEDS_REDSTONE() != null) {
            needsRedstone = true;
        }
        this.commandBuffer.setNeedsRedstone(needsRedstone);
    }

    @Override
    public void enterCommand(CommandContext ctx) {
        String command = ctx.COMMAND().getText();
        this.commandBuffer.setCommand(command);
    }

    private String lastExecuteIdentifier;

    @Override
    public void enterExecute(ExecuteContext ctx) {
        String method = ctx.IDENTIFIER().getText();
        String command = "/execute @e[name=" + method
                + "] ~ ~ ~ /setblock ~ ~ ~ redstone_block";
        this.commandBuffer.setCommand(command);
        lastExecuteIdentifier = method;
    }

    @Override
    public void enterWaitfor(WaitforContext ctx) { // FIXME: Fix waitfor for
                                                    // repeating context
        TerminalNode identifier = ctx.IDENTIFIER();
        String method;
        if (identifier != null) {
            method = identifier.getText();
        } else if (lastExecuteIdentifier != null) {
            method = lastExecuteIdentifier;
            lastExecuteIdentifier = null;
        } else {
            Token symbol = ctx.WAITFOR().getSymbol();
            throw new CompilerException(programFile, symbol.getLine(),
                    symbol.getStopIndex(),
                    "Missing Identifier. No previous execution was found to wait for.");
        }
        Boolean conditional = commandBuffer.getConditional();
        if (conditional == null) {
            conditional = false;
        }
        if (conditional) {
            commandBuffer
                    .setCommand("/summon ArmorStand ${this + 3} {CustomName:\""
                            + method
                            + RETURN
                            + "\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
            commands.add(this.commandBuffer.toCommand());
            commands.add(new Command("/blockdata ${this - 1} {SuccessCount:1}"));
            commands.add(new Command("/setblock ${this + 1} redstone_block",
                    true));
            commands.add(null);
            commands.add(new Command("/setblock ${this - 1} stone",
                    Mode.IMPULSE, false));
        } else {
            commandBuffer
                    .setCommand("/summon ArmorStand ${this + 1} {CustomName:\""
                            + method
                            + RETURN
                            + "\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
            commands.add(this.commandBuffer.toCommand());
            commands.add(null);
            commands.add(new Command("/setblock ${this - 1} stone",
                    Mode.IMPULSE, false));
        }
        commandBuffer = null;
    }

    @Override
    public void exitCommandDeclaration(CommandDeclarationContext ctx) {
        if (commandBuffer == null) {
            return;
        }
        Command command = commandBuffer.toCommand();
        commands.add(command);
        commandBuffer = null;

    }

    @Override
    public void enterSkip(SkipContext ctx) {
        this.commands.add(null);
    }

    @Override
    public void exitMethod(MethodContext ctx) {
        CommandChain chain = new CommandChain(this.getName(), this.commands);
        this.chains.add(chain);
        this.commands = null;
    }

    @Override
    public void exitSkript(SkriptContext ctx) {
        CommandChain chain = new CommandChain(null, this.commands);
        this.chains.add(chain);
        this.commands = null;
    }

}
