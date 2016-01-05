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

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.CommandChain;
import de.adrodoc55.minecraft.mpl.CompilerException;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.AutoContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ConditionalContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.InstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModusContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.NotifyDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProcessContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProgramContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProjectContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkipContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkriptContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.StartContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.StopContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.UninstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.WaitforContext;

public class MplInterpreter extends MplBaseListener {

    private static final String NOTIFY = "_NOTIFY";

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

    public File getProgramFile() {
        return programFile;
    }

    public boolean isProject() {
        return context.isProject();
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

    private ContextBuffer context;

    @Override
    public void enterInstall(InstallContext ctx) {
        context = new ContextBuffer(programFile);
    }

    @Override
    public void exitInstall(InstallContext ctx) {
        installation.addAll(context.getCommands());
        context = null;
    }

    @Override
    public void enterUninstall(UninstallContext ctx) {
        context = new ContextBuffer(programFile);
    }

    @Override
    public void exitUninstall(UninstallContext ctx) {
        uninstallation.addAll(context.getCommands());
        context = null;
    }

    @Override
    public void enterProject(ProjectContext ctx) {
        context = new ContextBuffer(programFile);
        context.setProject(true);
    }

    @Override
    public void exitProject(ProjectContext ctx) {
        context = null;
    }

    @Override
    public void enterProcess(ProcessContext ctx) {
        context = new ContextBuffer(programFile);
        context.setProcess(true);
        context.setName(ctx.IDENTIFIER().getText());
        if (ctx.REPEAT() != null) {
            context.setRepeatingProcess(true);
            context.setRepeatingContext(true);
        } else {
            context.setRepeatingProcess(false);
            context.setRepeatingContext(false);
        }
    }

    @Override
    public void enterSkript(SkriptContext ctx) {
        context = new ContextBuffer(programFile);
        context.setScript(true);
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
        Boolean conditional = null;
        if (ctx.UNCONDITIONAL() != null) {
            conditional = false;
        } else if (ctx.CONDITIONAL() != null) {
            conditional = true;
        } else if (ctx.INVERT() != null) {
            context.add(new Command("/blockdata ${this - 1} {SuccessCount:0}",
                    true));
            context.add(new Command("/blockdata ${this - 1} {SuccessCount:1}"));
            conditional = true;
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

    private String lastExecuteIdentifier;

    @Override
    public void enterStart(StartContext ctx) {
        String method = ctx.IDENTIFIER().getText();
        String command = "/execute @e[name=" + method
                + "] ~ ~ ~ /setblock ~ ~ ~ redstone_block";
        commandBuffer.setCommand(command);
        lastExecuteIdentifier = method;
    }

    @Override
    public void enterStop(StopContext ctx) {
        String method;
        if (ctx.IDENTIFIER() != null) {
            method = ctx.IDENTIFIER().getText();
        } else if (context.isRepeatingProcess()) {
            method = context.getName();
        } else {
            Token symbol = ctx.STOP().getSymbol();
            throw new CompilerException(programFile, symbol.getLine(),
                    symbol.getStartIndex(),
                    "Can only stop repeating processes.");
        }
        String command = "/execute @e[name=" + method
                + "] ~ ~ ~ /setblock ~ ~ ~ stone";
        commandBuffer.setCommand(command);
    }

    @Override
    public void enterNotifyDeclaration(NotifyDeclarationContext ctx) {
        if (!context.isProcess()) {
            Token symbol = ctx.NOTIFY().getSymbol();
            throw new CompilerException(programFile, symbol.getLine(),
                    symbol.getStartIndex(),
                    "Encountered notify outside of a process context.");
        }
        String method = context.getName();
        commandBuffer.setCommand("/execute @e[name=" + method + NOTIFY
                + "] ~ ~ ~ /setblock ~ ~ ~ redstone_block");
        context.add(commandBuffer.toCommand());
        commandBuffer.setCommand("/kill @e[name=" + method + NOTIFY + "]");
    }

    @Override
    public void enterWaitfor(WaitforContext ctx) {
        Token symbol = ctx.WAITFOR().getSymbol();
        if (context.isRepeatingContext()) {
            throw new CompilerException(programFile, symbol.getLine(),
                    symbol.getStartIndex(),
                    "Encountered waitfor in repeating context.");
        }
        TerminalNode identifier = ctx.IDENTIFIER();
        String method;
        if (identifier != null) {
            method = identifier.getText();
        } else if (lastExecuteIdentifier != null) {
            method = lastExecuteIdentifier;
            lastExecuteIdentifier = null;
        } else {
            throw new CompilerException(programFile, symbol.getLine(),
                    symbol.getStopIndex(),
                    "Missing Identifier. No previous start was found to wait for.");
        }
        Boolean conditional = commandBuffer.getConditional();
        if (conditional == null) {
            conditional = false;
        }
        if (conditional) {
            commandBuffer
                    .setCommand("/summon ArmorStand ${this + 3} {CustomName:\""
                            + method
                            + NOTIFY
                            + "\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
            context.add(commandBuffer.toCommand());
            context.add(new Command("/blockdata ${this - 1} {SuccessCount:1}"));
            context.add(new Command("/setblock ${this + 1} redstone_block",
                    true));
            context.add(null);
            context.add(new Command("/setblock ${this - 1} stone",
                    Mode.IMPULSE, false));
        } else {
            commandBuffer
                    .setCommand("/summon ArmorStand ${this + 1} {CustomName:\""
                            + method
                            + NOTIFY
                            + "\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
            context.add(commandBuffer.toCommand());
            context.add(null);
            context.add(new Command("/setblock ${this - 1} stone",
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
        context.add(command);
        commandBuffer = null;

    }

    @Override
    public void enterSkip(SkipContext ctx) {
        context.add(null);
    }

    @Override
    public void exitProcess(ProcessContext ctx) {
        LinkedList<Command> commands = context.getCommands();
        if (context.isRepeatingProcess()) {
            if (commands.size() > 0) {
                Command first = commands.get(0);
                first.setMode(Mode.REPEAT);
                first.setNeedsRedstone(true);
            }
        } else {
            commands.add(0, new Command("/setblock ${this - 1} stone",
                    Mode.IMPULSE, false));
        }
        CommandChain chain = new CommandChain(context.getName(), commands);
        chains.add(chain);
        context = null;
    }

    @Override
    public void exitSkript(SkriptContext ctx) {
        CommandChain chain = new CommandChain(null, context.getCommands());
        chains.add(chain);
        context = null;
    }

}
