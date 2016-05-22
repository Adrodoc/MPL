package de.adrodoc55.minecraft.mpl.placement;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.blocks.AirBlock;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.Transmitter;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;

@Ignore
public class MplProjectPlacerTest extends MplTestBase {

  @Test
  public void test_size() throws NotEnoughSpaceException {
    // given:
    int chainCount = several();
    List<ChainLink> list = listOf(some($Command()));
    ChainContainer container = some($ChainContainer()//
        .withChains($listOf(chainCount, $CommandChain()//
            .withCommands(list))));

    // when:
    List<CommandBlockChain> chains =
        new MplProgramPlacer(container, new CompilerOptions(TRANSMITTER)).place();

    // then:
    assertThat(chains.size()).isEqualTo(chainCount);
  }

  @Test
  public void test_unconditional_Start() throws NotEnoughSpaceException {
    // given:
    CommandChain chain = some($CommandChain().withCommands(listOf(//
        new Command("/say hi"))));

    ChainContainer container = some($ChainContainer()//
        .withChains(listOf(chain)));

    // when:
    List<CommandBlockChain> chains =
        new MplProgramPlacer(container, new CompilerOptions(TRANSMITTER)).place();

    // then:
    assertThat(chains.size()).isEqualTo(1);
    assertThat(chains.get(0).getBlocks()).containsExactly(//
        new Transmitter(false, new Coordinate3D()), //
        new CommandBlock((Command) chain.getCommands().get(0), Direction3D.EAST,
            new Coordinate3D(1, 0, 0)), //
        new AirBlock(new Coordinate3D(2, 0, 0))//
    );
  }

}
