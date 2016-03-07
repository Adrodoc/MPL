/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
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
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.chain_computing;

import static de.adrodoc55.TestBase.someInt;
import static de.adrodoc55.TestBase.someString;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Command;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$some;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.mpl.ChainPart;
import de.adrodoc55.minecraft.mpl.CommandBlock;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;
import de.adrodoc55.minecraft.mpl.MplBlock;

public abstract class ChainComputerTest {
  protected ChainComputer underTest;

  @Before
  public abstract void setup();

  @Test
  public void test_computeChain_Erzeugte_Kette_enthaelt_alle_Commandos_plus_einen_folgenden_leeren_Block() {
    // Given:
    String name = someString();
    List<ChainPart> commands = new ArrayList<>();

    int anzahl = someInt(10);
    commands.add($some($Command().withConditional(false)));
    for (int a = 1; a < anzahl; a++) {
      commands.add($some($Command()));
    }
    CommandChain input = new CommandChain(name, commands);

    // When:
    CommandBlockChain optimal = underTest.computeOptimalChain(input);
    // Then:
    commands.add(null); // folgender_leeren_Block

    List<MplBlock> blocks = optimal.getCommandBlocks();
    List<ChainPart> actual = Lists.transform(blocks, block -> {
      if (block instanceof CommandBlock) {
        CommandBlock commandBlock = (CommandBlock) block;
        return commandBlock.toCommand();
      }
      return null;
    });
    assertThat(actual).containsExactlyElementsOf(commands);
  }

  @Test
  public void test_computeChain_Erster_Command_darf_nicht_conditional_sein() {
    // Given:
    String name = someString();
    List<ChainPart> commands = new ArrayList<>();

    int anzahl = someInt(10);
    commands.add($some($Command().withConditional(true)));
    for (int a = 1; a < anzahl; a++) {
      commands.add($some($Command()));
    }
    CommandChain input = new CommandChain(name, commands);

    // When:
    try {
      underTest.computeOptimalChain(input);
      fail("No Exception thrown.");
    } catch (Exception ex) {
      // Then:
      assertThat(ex).isExactlyInstanceOf(IllegalStateException.class);
      assertThat(ex.getMessage()).isEqualTo("The first Command can't be conditional!");
    }

  }

  @Test
  // 7 + 1 = 8 = 2 ^ 3 (Der letzte Block muss Luft sein)
  public void test_computeChain_7_nicht_conditional_Commandos_ergeben_2x2x2_Würfel_und_enthaelt_alle_Commandos() {
    // Given:
    String name = someString();
    List<ChainPart> commands = new ArrayList<>();
    for (int a = 0; a < 7; a++) {
      commands.add($some($Command().withConditional(false)));
    }
    CommandChain input = new CommandChain(name, commands);

    // When:
    CommandBlockChain optimal = underTest.computeOptimalChain(input);
    // Then:
    commands.add(null); // folgender_leeren_Block
    // 2 ^ 3 Würfel
    assertThat(optimal.getMin()).isEqualTo(new Coordinate3D(0, 0, 0));
    assertThat(optimal.getMax()).isEqualTo(new Coordinate3D(1, 1, 1));
    // Enthält alle Commandos
    List<MplBlock> blocks = optimal.getCommandBlocks();
    List<ChainPart> actual = Lists.transform(blocks, block -> {
      if (block instanceof CommandBlock) {
        CommandBlock commandBlock = (CommandBlock) block;
        return commandBlock.toCommand();
      }
      return null;
    });
    assertThat(actual).containsExactlyElementsOf(commands);
  }

  @Test
  // 26 + 1 = 27 = 3 ^ 3 (Der letzte Block muss Luft sein)
  public void test_computeChain_26_nicht_conditional_Commandos_ergeben_3x3x3_Würfel_und_enthaelt_alle_Commandos() {
    // Given:
    String name = someString();
    List<ChainPart> commands = new ArrayList<>();
    for (int a = 0; a < 26; a++) {
      commands.add($some($Command().withConditional(false)));
    }
    CommandChain input = new CommandChain(name, commands);

    // When:
    ExecutorService executor = Executors.newCachedThreadPool();
    Callable<CommandBlockChain> task = new Callable<CommandBlockChain>() {
      public CommandBlockChain call() {
        return underTest.computeOptimalChain(input);
      }
    };
    Future<CommandBlockChain> future = executor.submit(task);
    CommandBlockChain optimal = null;
    try {
      optimal = future.get(5, TimeUnit.SECONDS);
    } catch (TimeoutException ex) {
      fail("TimeoutException: " + ex);
    } catch (InterruptedException ex) {
      fail("InterruptedException: " + ex);
    } catch (ExecutionException ex) {
      fail("ExecutionException: " + ex);
    } finally {
      future.cancel(true); // may or may not desire this
    }
    // Then:
    commands.add(null); // folgender_leeren_Block
    // 2 ^ 3 Würfel
    assertThat(optimal.getMin()).isEqualTo(new Coordinate3D(0, 0, 0));
    assertThat(optimal.getMax()).isEqualTo(new Coordinate3D(2, 2, 2));
    // Enthält alle Commandos
    List<MplBlock> blocks = optimal.getCommandBlocks();
    List<ChainPart> actual = Lists.transform(blocks, block -> {
      if (block instanceof CommandBlock) {
        CommandBlock commandBlock = (CommandBlock) block;
        return commandBlock.toCommand();
      }
      return null;
    });
    assertThat(actual).containsExactlyElementsOf(commands);
  }

  @Test
  // Conditional Commandos dürfen nicht geknickt werden.
  public void test_computeChain_7_conditional_Commandos_ergibt_Kette_und_enthaelt_alle_Commandos() {
    // Given:
    String name = someString();
    List<ChainPart> commands = new ArrayList<>();
    commands.add($some($Command().withConditional(false)));
    for (int a = 1; a < 7; a++) {
      commands.add($some($Command().withConditional(true)));
    }
    CommandChain input = new CommandChain(name, commands);

    // When:
    CommandBlockChain optimal = underTest.computeOptimalChain(input);
    // Then:
    commands.add(null); // folgender_leeren_Block
    // 7er Kette Würfel
    assertThat(optimal.getMin()).isEqualTo(new Coordinate3D(0, 0, 0));
    assertThat(optimal.getMax()).isEqualTo(new Coordinate3D(7, 0, 0));
    // Enthält alle Commandos
    List<MplBlock> blocks = optimal.getCommandBlocks();
    List<ChainPart> actual = Lists.transform(blocks, block -> {
      if (block instanceof CommandBlock) {
        CommandBlock commandBlock = (CommandBlock) block;
        return commandBlock.toCommand();
      }
      return null;
    });
    assertThat(actual).containsExactlyElementsOf(commands);
  }

  @Test
  // Conditional Commandos dürfen nicht geknickt werden.
  public void test_computeChain_conditional_Commandos_werden_nicht_geknickt_und_Kette_enthaelt_alle_Commandos() {
    // Given:
    String name = someString();
    List<ChainPart> commands = new ArrayList<>();
    commands.add($some($Command().withConditional(false)));
    for (int a = 1; a < 7; a++) {
      commands.add($some($Command()));
    }
    CommandChain input = new CommandChain(name, commands);

    // When:
    CommandBlockChain optimal = underTest.computeOptimalChain(input);
    // Then:
    commands.add(null); // folgender_leeren_Block

    List<MplBlock> commandBlocks = optimal.getCommandBlocks();
    for (int a = 0; a < commandBlocks.size(); a++) {
      MplBlock block = commandBlocks.get(a);
      if (block instanceof CommandBlock) {
        CommandBlock commandBlock = (CommandBlock) block;
        if (commandBlock.isConditional()) {
          Coordinate3D currentCoordinate = commandBlock.getCoordinate();
          Coordinate3D relativeCoordinate = commandBlock.getDirection().toCoordinate();

          Coordinate3D expectedPreviousCoordinate = currentCoordinate.minus(relativeCoordinate);
          Coordinate3D actualPreviousCoordinate = commandBlocks.get(a - 1).getCoordinate();
          assertThat(actualPreviousCoordinate).isEqualTo(expectedPreviousCoordinate);

          if (a + 1 < commandBlocks.size()) {
            Coordinate3D expectedNextCoordinate = currentCoordinate.plus(relativeCoordinate);
            Coordinate3D actualNextCoordinate = commandBlocks.get(a + 1).getCoordinate();
            assertThat(actualNextCoordinate).isEqualTo(expectedNextCoordinate);
          }
        }
      }
    }
    // Enthält alle Commandos
    List<MplBlock> blocks = commandBlocks;
    List<ChainPart> actual = Lists.transform(blocks, block -> {
      if (block instanceof CommandBlock) {
        CommandBlock commandBlock = (CommandBlock) block;
        return commandBlock.toCommand();
      }
      return null;
    });
    assertThat(actual).containsExactlyElementsOf(commands);
  }

  @Test
  public void test_computeChain_Erzeugte_Kette_ist_leer_wenn_initiale_kette_leer_war() {
    // Given:
    String name = someString();
    List<ChainPart> commands = new ArrayList<>();
    CommandChain input = new CommandChain(name, commands);

    // When:
    CommandBlockChain optimal = underTest.computeOptimalChain(input);
    // Then:

    List<MplBlock> actual = optimal.getCommandBlocks();
    assertThat(actual).isEmpty();
  }

}
