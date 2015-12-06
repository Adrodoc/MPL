package de.adrodoc55.minecraft.mpl.antlr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.CommandChain;
import de.adrodoc55.minecraft.mpl.Program;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.AutoContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ConditionalContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeAtContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeMaxContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModusContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProgramContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkipDeclarationContext;

public class MplCompiler extends MplBaseListener {

    public static Program compile(File programFile) throws IOException {
        ProgramContext ctx = interpret(programFile);
        MplCompiler compiler = new MplCompiler(programFile, new Coordinate3D());
        new ParseTreeWalker().walk(compiler, ctx);
        Program program = new Program(compiler.chains);
        return program;
    }

    public static ProgramContext interpret(File programFile) throws IOException {
        BufferedReader reader = Files.newBufferedReader(programFile.toPath());
        ANTLRInputStream input = new ANTLRInputStream(reader);
        MplLexer lexer = new MplLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        MplParser parser = new MplParser(tokens);
        ProgramContext context = parser.program();
        return context;
    }

    private final File programFile;
    private final Coordinate3D start;

    public MplCompiler(File programFile, Coordinate3D start) {
        this.programFile = programFile;
        this.start = start;
    }

    private String getName() {
        String fileName = programFile.getName();
        int idx = fileName.lastIndexOf('.');
        String name = (idx == -1) ? fileName : fileName.substring(0, idx);
        return name;
    }

    private Map<Coordinate3D, CommandChain> chains = new HashMap<Coordinate3D, CommandChain>();

    private LinkedList<Command> commands;

    @Override
    public void visitErrorNode(ErrorNode node) {
        throw new GrammarException("Mismatched input: '" + node.getText() + "'");
    }

    @Override
    public void enterProgram(ProgramContext ctx) {
        this.commands = new LinkedList<Command>();
    }

    @Override
    public void exitProgram(ProgramContext ctx) {
        CommandChain chain = new CommandChain(this.getName(), this.commands);
        this.chains.put(start, chain);
        this.commands = null;
    }

    @Override
    public void enterSkipDeclaration(SkipDeclarationContext ctx) {
        this.commands.add(null);
    }

    private CommandBuffer commandBuffer;

    @Override
    public void enterCommandDeclaration(CommandDeclarationContext ctx) {
        this.commandBuffer = new CommandBuffer();
    }

    @Override
    public void exitCommandDeclaration(CommandDeclarationContext ctx) {
        Command command = this.commandBuffer.toCommand();
        this.commands.add(command);
        this.commandBuffer = null;
    }

    @Override
    public void enterModus(ModusContext ctx) {
        Mode mode = Mode.valueOf(ctx.getText().toUpperCase());
        this.commandBuffer.setMode(mode);
    }

    @Override
    public void enterConditional(ConditionalContext ctx) {
        Boolean conditional = true;
        this.commandBuffer.setConditional(conditional);
    }

    @Override
    public void enterAuto(AutoContext ctx) {
        if (ctx.ALWAYS_ACTIVE() != null) {
            Boolean needsRedstone = false;
            this.commandBuffer.setNeedsRedstone(needsRedstone);
        } else if (ctx.NEEDS_REDSTONE() != null) {
            Boolean needsRedstone = true;
            this.commandBuffer.setNeedsRedstone(needsRedstone);
        } else {
            throw new GrammarException("Encountered '" + ctx.getText()
                    + "' within AutoContext!");
        }
    }

    @Override
    public void enterCommand(CommandContext ctx) {
        String command = ctx.COMMAND().getText();
        this.commandBuffer.setCommand(command);
    }

    private static class CommandBuffer {
        private String command;
        private Mode mode;
        private Boolean conditional;
        private Boolean needsRedstone;

        public Command toCommand() {
            return new Command(command, mode, conditional, needsRedstone);
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
        }

        public void setConditional(Boolean conditional) {
            this.conditional = conditional;
        }

        public void setNeedsRedstone(Boolean needsRedstone) {
            this.needsRedstone = needsRedstone;
        }
    }

    private IncludeBuffer includeBuffer;

    @Override
    public void enterIncludeDeclaration(IncludeDeclarationContext ctx) {
        this.includeBuffer = new IncludeBuffer();
    }

    @Override
    public void exitIncludeDeclaration(IncludeDeclarationContext ctx) {
        String includeName = MplLexerUtils.getContainedString(ctx.STRING());
        this.includeBuffer.setIncludeName(includeName);
        Map<Coordinate3D, CommandChain> includeChains = this.includeBuffer
                .toInclude();
        this.chains.putAll(includeChains);
        this.includeBuffer = null;
    }

    @Override
    public void enterIncludeAt(IncludeAtContext ctx) {
        List<TerminalNode> unsignedInt = ctx.coordinate().UNSIGNED_INT();
        if (unsignedInt.size() != 3) {
            throw new GrammarException(
                    "Encountered not exactly 3 UNSIGNED_INT in coordinate of IncludeAtContext!");
        }
        int x = Integer.parseInt(unsignedInt.get(0).getText());
        int y = Integer.parseInt(unsignedInt.get(1).getText());
        int z = Integer.parseInt(unsignedInt.get(2).getText());
        Coordinate3D at = new Coordinate3D(x, y, z);
        this.includeBuffer.setAt(at);
    }

    @Override
    public void enterIncludeMax(IncludeMaxContext ctx) {
        List<TerminalNode> unsignedInt = ctx.coordinate().UNSIGNED_INT();
        if (unsignedInt.size() != 3) {
            throw new GrammarException(
                    "Encountered not exactly 3 UNSIGNED_INT in coordinate of IncludeMaxContext!");
        }
        int x = Integer.parseInt(unsignedInt.get(0).getText());
        int y = Integer.parseInt(unsignedInt.get(1).getText());
        int z = Integer.parseInt(unsignedInt.get(2).getText());
        Coordinate3D max = new Coordinate3D(x, y, z);
        this.includeBuffer.setMax(max);
    }

    private class IncludeBuffer {
        private String includeName;
        private Coordinate3D at;
        private Coordinate3D max; // TODO: include max() implementieren

        public Map<Coordinate3D, CommandChain> toInclude() {
            File includeFile = new File(
                    MplCompiler.this.programFile.getParentFile(),
                    this.includeName);
            Program include;
            try {
                include = compile(includeFile);
            } catch (Exception ex) {
                throw new CompilerException("Couldn't include '"
                        + this.includeName + "'", ex);
            }
            Map<Coordinate3D, CommandChain> includeChains = new HashMap<Coordinate3D, CommandChain>();
            Set<Entry<Coordinate3D, CommandChain>> entrySet = include
                    .getChains().entrySet();
            for (Entry<Coordinate3D, CommandChain> entry : entrySet) {
                Coordinate3D at = this.at;
                if (at == null) {
                    at = new Coordinate3D();
                }
                Coordinate3D chainStart = at.plus(entry.getKey());
                CommandChain chain = entry.getValue();
                includeChains.put(chainStart, chain);
            }
            return includeChains;
        }

        public void setIncludeName(String includeName) {
            this.includeName = includeName;
        }

        public void setAt(Coordinate3D at) {
            this.at = at;
        }

        public void setMax(Coordinate3D max) {
            this.max = max;
        }
    }
}
