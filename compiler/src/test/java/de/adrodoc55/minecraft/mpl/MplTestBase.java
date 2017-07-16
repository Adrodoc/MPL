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
package de.adrodoc55.minecraft.mpl;

import static de.adrodoc55.minecraft.coordinate.Direction3D.DOWN;
import static de.adrodoc55.minecraft.coordinate.Direction3D.EAST;
import static de.adrodoc55.minecraft.coordinate.Direction3D.NORTH;
import static de.adrodoc55.minecraft.coordinate.Direction3D.SOUTH;
import static de.adrodoc55.minecraft.coordinate.Direction3D.UP;
import static de.adrodoc55.minecraft.coordinate.Direction3D.WEST;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newCommand;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.v4.runtime.CommonToken;

import de.adrodoc55.minecraft.coordinate.Coordinate3DBuilder;
import de.adrodoc55.minecraft.coordinate.Orientation3DBuilder;
import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.ProcessType;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpointBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCallBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommandBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIfBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplInterceptBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotifyBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStartBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStopBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitforBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreakBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinueBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhileBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcessBuilder;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgramBuilder;
import de.adrodoc55.minecraft.mpl.ast.variable.MplStringVariableBuilder;
import de.adrodoc55.minecraft.mpl.ast.variable.type.MplType;
import de.adrodoc55.minecraft.mpl.chain.ChainContainerBuilder;
import de.adrodoc55.minecraft.mpl.chain.CommandChainBuilder;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.CommandBuilder;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkipBuilder;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ProcessCommandsHelper;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContextBuilder;
import de.adrodoc55.minecraft.mpl.compilation.MplSourceBuilder;
import de.adrodoc55.minecraft.mpl.interpretation.CommandPartBuffer;
import de.adrodoc55.minecraft.mpl.interpretation.CommandPartBufferBuilder;
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBufferBuilder;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;
import net.karneim.pojobuilder.Builder;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;
import net.karneim.pojobuilder.GeneratePojoBuilder;

public class MplTestBase extends MplAssertionFactory {

  public static Builder<String> $Identifier() {
    return new Builder<String>() {
      @Override
      public String build() {
        return "Identifier_" + somePositiveInt();
      }
    };
  }

  public Builder<String> $CommandString() {
    return new Builder<String>() {
      @Override
      public String build() {
        return "command " + some($String());
      }
    };
  }

  public Builder<String> $Selector() {
    return new Builder<String>() {
      @Override
      public String build() {
        return "@e[name=" + some($Identifier()) + "]";
      }
    };
  }

  public OneOf<Mode> $Mode() {
    return $oneOf(Mode.values());
  }

  public OneOf<Conditional> $Conditional() {
    return $oneOf(Conditional.values());
  }

  public OneOf<ProcessType> $ProcessType() {
    return $oneOf(ProcessType.values());
  }

  public static MplCompilerContextBuilder $MplCompilerContext() {
    return new MplCompilerContextBuilder()//
        .withOptions(new CompilerOptions())//
        .withVersion(MinecraftVersion.getDefault())//
    ;
  }

  public CommandPartBufferBuilder $CommandPartBuffer() {
    return new CommandPartBufferBuilder()//
        .withCommand($CommandString())//
    ;
  }

  public CommandBuilder $Command() {
    return new CommandBuilder()//
        .withMinecraftCommand($CommandPartBuffer())//
        .withMode($Mode())//
        .withConditional($boolean())//
        .withNeedsRedstone($boolean())//
    ;
  }

  private ModifierBufferBuilder $ModifierBuffer() {
    return new ModifierBufferBuilder()//
        .withMode($Mode())//
        .withConditional($Conditional())//
        .withNeedsRedstone($boolean())//
    ;
  }

  protected MplSourceBuilder $MplSource() {
    return new MplSourceBuilder()//
        .withFile(new File(""))//
        .withToken(new CommonToken(MplLexer.PROCESS))//
        .withLine($String())//
    ;
  }

  public MplCommandBuilder $MplCommand() {
    return new MplCommandBuilder()//
        .withModifier($ModifierBuffer())//
        .withCommand($CommandString())//
        .withSource($MplSource())//
    ;
  }

  public MplProgramBuilder $MplProgram() {
    return new MplProgramBuilder()//
        .withProgramFile(new File(""))//
        .withContext($MplCompilerContext())//
        .withName($String())//
        .withScript($boolean())//
        .withOrientation($Orientation3D())//
        .withInstall($MplProcess()//
            .withType(ProcessType.IMPULSE))//
        .withUninstall($MplProcess()//
            .withType(ProcessType.IMPULSE))//
        .withMax($Coordinate3D())//
    ;
  }

  public MplProcessBuilder $MplProcess() {
    return new MplProcessBuilder()//
        .withName($Identifier())//
        .withTags(new ArrayList<>())//
        .withType($oneOf(ProcessType.IMPULSE, ProcessType.REPEAT))//
        .withSource($MplSource())//
    ;
  }

  public MplCallBuilder $MplCall() {
    return new MplCallBuilder()//
        .withModifier($ModifierBuffer())//
        .withProcess($Identifier())//
        .withSource($MplSource())//
    ;
  }

  public MplStartBuilder $MplStart() {
    return new MplStartBuilder()//
        .withModifier($ModifierBuffer())//
        .withSelector($Selector())//
        .withSource($MplSource())//
    ;
  }

  public MplStopBuilder $MplStop() {
    return new MplStopBuilder()//
        .withModifier($ModifierBuffer())//
        .withSelector($Selector())//
        .withSource($MplSource())//
    ;
  }

  public MplWaitforBuilder $MplWaitfor() {
    return new MplWaitforBuilder()//
        .withModifier($ModifierBuffer())//
        .withEvent($String())//
        .withSource($MplSource())//
    ;
  }

  public MplNotifyBuilder $MplNotify() {
    return new MplNotifyBuilder()//
        .withModifier($ModifierBuffer())//
        .withEvent($String())//
        .withSource($MplSource())//
    ;
  }

  public MplInterceptBuilder $MplIntercept() {
    return new MplInterceptBuilder()//
        .withModifier($ModifierBuffer())//
        .withEvent($String())//
        .withSource($MplSource())//
    ;
  }

  public MplBreakpointBuilder $MplBreakpoint() {
    return new MplBreakpointBuilder()//
        .withModifier($ModifierBuffer())//
        .withMessage($String())//
        .withSource($MplSource())//
    ;
  }

  public MplSkipBuilder $MplSkip() {
    return new MplSkipBuilder()//
        .withInternal($boolean())//
    ;
  }

  public MplIfBuilder $MplIf() {
    return new MplIfBuilder()//
        .withNot($boolean())//
        .withCondition($CommandString())//
        .withSource($MplSource())//
    ;
  }

  public MplWhileBuilder $MplWhile() {
    return new MplWhileBuilder()//
        .withNot($boolean())//
        .withTrailing($boolean())//
        .withCondition($CommandString())//
        .withSource($MplSource())//
    ;
  }

  public MplBreakBuilder $MplBreak() {
    return new MplBreakBuilder()//
        .withModifier($ModifierBuffer())//
        .withLabel($String())//
        .withSource($MplSource())//
    ;
  }

  public MplContinueBuilder $MplContinue() {
    return new MplContinueBuilder()//
        .withModifier($ModifierBuffer())//
        .withLabel($String())//
        .withSource($MplSource())//
    ;
  }

  public OneOf<MplType<?>> $MplType() {
    return $oneOf(MplType.values());
  }

  public MplStringVariableBuilder $MplVariable() {
    return new MplStringVariableBuilder()//
        .withDeclarationSource($MplSource())//
        .withIdentifier($Identifier())//
        .withValue($String())//
    ;
  }

  public Orientation3DBuilder $Orientation3D() {
    return new Orientation3DBuilder()//
        .withA($oneOf(EAST, WEST))//
        .withB($oneOf(UP, DOWN))//
        .withC($oneOf(SOUTH, NORTH))//
    ;
  }

  public Coordinate3DBuilder $Coordinate3D() {
    return new Coordinate3DBuilder()//
        .withX(many())//
        .withY(many())//
        .withZ(many())//
    ;
  }

  public ChainContainerBuilder $ChainContainer(CompilerOption... options) {
    return $ChainContainer(new CompilerOptions(options));
  }

  public ChainContainerBuilder $ChainContainer(CompilerOptions options) {
    return new ChainContainerBuilder()//
        .withOrientation($Orientation3D())//
        .withMax($Coordinate3D().withX(-1).withY(-1).withZ(-1))//
        .withInstall($CommandChain(options)//
            .withName("install")//
            .withCommands($validChainCommands(options)//
                .withCommands(new ArrayList<>())))//
        .withUninstall($CommandChain(options)//
            .withName("uninstall")//
            .withCommands($validChainCommands(options)//
                .withCommands(new ArrayList<>())))//
        .withChains(new ArrayList<>())//
        .withHashCode($String())//
    ;
  }

  public CommandChainBuilder $CommandChain(CompilerOption... options) {
    return $CommandChain(new CompilerOptions(options));
  }

  public CommandChainBuilder $CommandChain(CompilerOptions options) {
    return new CommandChainBuilder()//
        .withName($String())//
        .withCommands($validChainCommands(options))//
    ;
  }

  public ValidCommandChainBuilder $validChainCommands(CompilerOption... options) {
    return $validChainCommands(new CompilerOptions(options));
  }

  public ValidCommandChainBuilder $validChainCommands(CompilerOptions options) {
    return new ValidCommandChainBuilder().withOptions(options);
  }

  @GeneratePojoBuilder(intoPackage = "de.adrodoc55.minecraft.mpl",
      withName = "ValidCommandChainBuilder")
  @GenerateMplPojoBuilder
  public static List<ChainLink> validChainCommands(MplTestBase $, CompilerOptions options,
      Collection<? extends ChainLink> commands) {
    ProcessCommandsHelper helper = new ProcessCommandsHelper(options);
    List<ChainLink> result = new ArrayList<>();
    if (options.hasOption(TRANSMITTER)) {
      result.add(new MplSkip());
      CommandPartBuffer cpb = new CommandPartBuffer();
      cpb.add(helper.getStopCommandHeader());
      cpb.add(new RelativeThisInsert(-1));
      cpb.add(helper.getStopCommandTrailer());
      result.add(newCommand(cpb, modifier(IMPULSE)));
    } else {
      result.add(newCommand(helper.getStopCommand(), modifier(IMPULSE)));
    }
    if (commands != null && !commands.isEmpty()) {
      result.add(some($.$Command()));
      result.addAll(commands);
    }
    return result;
  }

}
