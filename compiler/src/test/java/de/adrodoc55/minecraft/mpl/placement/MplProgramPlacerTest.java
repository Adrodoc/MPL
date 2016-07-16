package de.adrodoc55.minecraft.mpl.placement;

import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.blocks.AirBlock;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.Transmitter;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;

public class MplProgramPlacerTest extends MplTestBase {

  @Test
  public void test_size() throws NotEnoughSpaceException {
    // given:
    CompilerOption[] options = {TRANSMITTER};

    int chainCount = several();
    List<ChainLink> list = validChainCommands(options);
    list.addAll(listOf(some($Command())));
    ChainContainer container = some($ChainContainer(options)//
        .withOrientation(new Orientation3D())//
        .withChains($listOf(chainCount, $CommandChain()//
            .withCommands(list))));

    // when:
    List<CommandBlockChain> chains =
        new MplProgramPlacer(container, new CompilerOptions(options)).place();

    // then:
    assertThat(chains.size()).isEqualTo(chainCount + 2); // +2 for install and uninstall
  }

  @Test
  public void test_unconditional_Start() throws NotEnoughSpaceException {
    // given:
    CompilerOption[] options = {TRANSMITTER};

    CommandChain chain = some($CommandChain().withCommands(listOf(//
        new MplSkip(), //
        new Command("/say hi", IMPULSE))));

    ChainContainer container = some($ChainContainer(options)//
        .withOrientation(new Orientation3D())//
        .withChains(listOf(chain)));

    // when:
    List<CommandBlockChain> chains =
        new MplProgramPlacer(container, new CompilerOptions(options)).place();

    // then:
    assertThat(chains.size()).isEqualTo(1 + 2); // +2 for install and uninstall
    assertThat(chains.get(0).getBlocks()).containsExactly(//
        new Transmitter(false, new Coordinate3D(0, 0, 1)), //
        new CommandBlock((Command) chain.getCommands().get(1), Direction3D.EAST,
            new Coordinate3D(1, 0, 1)), //
        new AirBlock(new Coordinate3D(2, 0, 1))//
    );
  }

}
