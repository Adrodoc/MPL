package de.adrodoc55.minecraft.mpl.placement;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

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

public class MplProgramPlacerTest extends MplTestBase {

  @Test
  public void test_chains_size() throws NotEnoughSpaceException {
    // given:
    CompilerOptions options =
        some($boolean()) ? new CompilerOptions(TRANSMITTER) : new CompilerOptions();

    int chainCount = several();
    ChainContainer container = some($ChainContainer(options)//
        .withChains($listOf(chainCount, $CommandChain(options))));

    // when:
    List<CommandBlockChain> chains = new MplProgramPlacer(container, options).place();

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
    List<CommandBlockChain> chains = new MplProgramPlacer(container, options).place();

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
    List<CommandBlockChain> chains = new MplProgramPlacer(container, options).place();

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
    List<CommandBlockChain> chains = new MplProgramPlacer(container, options).place();

    // then:
    CommandBlockChain placed = findChain(chain.getName(), chains);

    int i = 0;
    for (MplBlock block : placed.getBlocks()) {
      assertThat(block.getCoordinate()).isEqualTo(new Coordinate3D(i++, 0, 1));
    }
  }

  private CommandBlockChain findChain(String name, List<CommandBlockChain> chains) {
    return chains.stream().filter(c -> name.equals(c.getName())).findFirst().get();
  }

}
