package de.adrodoc55.minecraft.mpl.compilation;

import static de.adrodoc55.TestBase.listOf;
import static de.adrodoc55.TestBase.some;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$ChainContainer;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Command;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$CommandChain;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;

public class MplCompilerTest {

  @Test
  public void testPlacement_Prozess_ArmorStands_befinden_sich_oben_in_jedem_Block()
      throws Exception {
    // given:
    List<ChainLink> commands = listOf(some($Command()));
    CommandChain chain = some($CommandChain().withCommands(commands));
    ChainContainer container = some($ChainContainer()//
        .withOrientation(new Orientation3D())//
        .withChains(listOf(chain)));

    // when:
    List<CommandBlockChain> placed = MplCompiler.place(container, new CompilerOptions());

    // then:
    assertThat(placed).hasSize(3);
    CommandBlockChain install =
        placed.stream().filter(c -> "install".equals(c.getName())).findFirst().get();
    List<MplBlock> blocks = install.getBlocks();
    assertThat(blocks).hasSize(3);
    CommandBlock block = (CommandBlock) blocks.get(1);
    assertThat(block.getCommand()).startsWith("summon ArmorStand ${origin + (0 0.4 1)} {");
  }
}
