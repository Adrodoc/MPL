package de.adrodoc55.minecraft.mpl.compilation;

import static de.adrodoc55.TestBase.listOf;
import static de.adrodoc55.TestBase.some;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$ChainContainer;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$CommandChain;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;

public class MplCompilerTest {

  @Test
  public void testPlacement_Prozess_ArmorStands_befinden_sich_oben_in_jedem_Block()
      throws Exception {
    // given:
    ChainContainer container = some($ChainContainer()//
        .withOrientation(new Orientation3D())//
        .withChains(listOf(some($CommandChain()))));

    // when:
    List<CommandBlockChain> placed = MplCompiler.place(container, new CompilerOptions());

    // then:
    assertThat(placed).hasSize(3);
    CommandBlockChain install =
        placed.stream().filter(c -> "install".equals(c.getName())).findFirst().get();
    List<MplBlock> blocks = install.getBlocks();
    assertThat(blocks).hasSize(3);
    CommandBlock block = (CommandBlock) blocks.get(1);
    assertThat(block.getCommand()).startsWith("summon ArmorStand ${origin + (0 0.4 1)}");
  }

  @Test
  public void testPlacement_Im_debug_mode_ohne_Transmitter_werden_keine_Marker_ArmorStands_fuer_Prozesse_benutzt_und_die_ArmorStands_befinden_sich_unten()
      throws Exception {
    // given:
    ChainContainer container = some($ChainContainer(DEBUG)//
        .withOrientation(new Orientation3D())//
        .withChains(listOf(some($CommandChain(DEBUG)))));

    // when:
    List<CommandBlockChain> placed = MplCompiler.place(container, new CompilerOptions(DEBUG));

    // then:
    assertThat(placed).hasSize(3);
    CommandBlockChain install =
        placed.stream().filter(c -> "install".equals(c.getName())).findFirst().get();
    List<MplBlock> blocks = install.getBlocks();
    assertThat(blocks).hasSize(3);
    CommandBlock block = (CommandBlock) blocks.get(1);
    String command = block.getCommand();
    assertThat(command).startsWith("summon ArmorStand ${origin + (0 -0.4 5)}");
    assertThat(command).doesNotContain("Marker");
  }

}
