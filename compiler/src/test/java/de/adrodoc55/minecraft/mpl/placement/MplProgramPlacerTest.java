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

import static de.adrodoc55.minecraft.mpl.MplTestUtils.findByName;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;
import de.adrodoc55.minecraft.mpl.version.MplVersion;

public class MplProgramPlacerTest extends AbstractMplProgramPlacerTest {
  @Override
  protected boolean isDebug() {
    return false;
  }

  @Override
  protected MplProgramPlacer createPlacer(MplVersion version, CompilerOptions options,
      ChainContainer container) {
    return new MplProgramPlacer(container, version, options);
  }

  @Test
  public void test_when_using_normal_Mode_Prozess_MarkerEntities_are_at_the_top_of_each_block()
      throws Exception {
    // given:
    MplVersion version = MinecraftVersion.getDefault();
    CompilerOptions options = new CompilerOptions();

    ChainContainer container = some($ChainContainer(options)//
        .withOrientation(new Orientation3D())//
        .withChains(listOf(some($CommandChain(options)))));

    // when:
    List<CommandBlockChain> placed = createPlacer(version, options, container).place();

    // then:
    CommandBlockChain install = findByName("install", placed);
    List<MplBlock> blocks = install.getBlocks();
    assertThat(blocks).hasSize(3);
    CommandBlock block = (CommandBlock) blocks.get(1);
    assertThat(block.getCommand())
        .startsWith("summon " + version.getMarkerEntityName() + " ${origin + (0 0.4 1)}");
  }

}
