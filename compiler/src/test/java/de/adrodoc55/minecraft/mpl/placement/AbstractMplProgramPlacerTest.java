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
package de.adrodoc55.minecraft.mpl.placement;

import static de.adrodoc55.minecraft.mpl.MplTestUtils.findChain;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Joiner;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.blocks.AirBlock;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;

public abstract class AbstractMplProgramPlacerTest extends MplTestBase {

  protected abstract boolean isDebug();

  protected abstract MplChainPlacer createPlacer(CompilerOptions options, ChainContainer container);

  @Test
  public void test_chains_size() throws NotEnoughSpaceException {
    // given:
    CompilerOptions options =
        some($boolean()) ? new CompilerOptions(TRANSMITTER) : new CompilerOptions();

    int chainCount = several();
    ChainContainer container = some($ChainContainer(options)//
        .withChains($listOf(chainCount, $CommandChain(options))));

    // when:
    List<CommandBlockChain> chains = createPlacer(options, container).place();

    // then:
    assertThat(chains.size()).isEqualTo(chainCount + 2); // +2 for install and uninstall
  }

  @Test
  public void test_chain_has_trailing_air() throws NotEnoughSpaceException {
    // given:
    CompilerOptions options =
        some($boolean()) ? new CompilerOptions(TRANSMITTER) : new CompilerOptions();

    CommandChain chain = some($CommandChain(options));
    ChainContainer container = some($ChainContainer(options)//
        .withChains(listOf(chain)));

    // when:
    List<CommandBlockChain> chains = createPlacer(options, container).place();

    // then:
    CommandBlockChain placed = findChain(chain.getName(), chains);
    List<MplBlock> blocks = placed.getBlocks();
    assertThat(blocks.get(blocks.size() - 1)).isExactlyInstanceOf(AirBlock.class);
  }

  @Test
  public void test_chain_contains_all_commands_in_order() throws NotEnoughSpaceException {
    // given:
    CompilerOptions options =
        some($boolean()) ? new CompilerOptions(TRANSMITTER) : new CompilerOptions();

    Collection<ChainLink> commands = some($validChainCommands(options));
    CommandChain chain = some($CommandChain(options).withCommands(commands));
    ChainContainer container = some($ChainContainer(options)//
        .withChains(listOf(chain)));

    // when:
    List<CommandBlockChain> chains = createPlacer(options, container).place();

    // then:
    CommandBlockChain placed = findChain(chain.getName(), chains);
    List<Command> actCommands = placed.getBlocks().stream().filter(b -> b instanceof CommandBlock)
        .map(c -> ((CommandBlock) c).toCommand()).collect(toList());
    List<Command> expCommand =
        commands.stream().filter(c -> c instanceof Command).map(c -> (Command) c).collect(toList());

    assertThat(actCommands).containsSubsequence(expCommand.toArray(new Command[0]));
  }

  @Test
  public void test_coordinates_are_in_a_straight_line_when_b_eq_1() throws NotEnoughSpaceException {
    // given:
    CompilerOptions options =
        some($boolean()) ? new CompilerOptions(TRANSMITTER) : new CompilerOptions();

    CommandChain chain = some($CommandChain(options));
    ChainContainer container = some($ChainContainer(options)//
        .withOrientation(new Orientation3D())//
        .withMax(new Coordinate3D(-1, 1, 0))//
        .withChains(listOf(chain)));

    // when:
    List<CommandBlockChain> chains = createPlacer(options, container).place();

    // then:
    CommandBlockChain placed = findChain(chain.getName(), chains);

    int z = isDebug() ? 5 : 1;
    int i = 0;
    for (MplBlock block : placed.getBlocks()) {
      assertThat(block.getCoordinate()).isEqualTo(new Coordinate3D(i++, 0, z));
    }
  }

  @Test
  public void test_a_process_with_tags_is_summoned_with_those_tags()
      throws NotEnoughSpaceException {
    // given:
    CompilerOptions options =
        some($boolean()) ? new CompilerOptions(TRANSMITTER) : new CompilerOptions();

    List<String> tags = some($listOf(several(), $Identifier()));
    ChainContainer container = some($ChainContainer(options)//
        .withChains($listOf(1, $CommandChain(options)//
            .withTags(tags))));

    // when:
    List<CommandBlockChain> chains = createPlacer(options, container).place();

    // then:
    CommandBlockChain install = findChain("install", chains);

    int index = options.hasOption(TRANSMITTER) ? 2 : 1;
    CommandBlock summon = (CommandBlock) install.getBlocks().get(index);
    assertThat(summon.getCommand())
        .contains("Tags:[" + container.getHashCode() + "," + Joiner.on(",").join(tags) + "]");
  }

}
