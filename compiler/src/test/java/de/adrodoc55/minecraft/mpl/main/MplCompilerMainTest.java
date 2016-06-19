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
package de.adrodoc55.minecraft.mpl.main;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DELETE_ON_UNINSTALL;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;

public class MplCompilerMainTest {

  @Test
  public void test_parseOptions__with_valid_options() throws Exception {
    // when:
    CompilerOptions options = MplCompilerMain.parseOptions("debug");

    // then:
    assertThat(options.hasOption(DEBUG)).isTrue();
    assertThat(options.getOptions()).containsOnly(DEBUG);
  }

  @Test
  public void test_parseOptions__with_invalid_options() {
    // when:
    InvalidOptionException act = null;
    try {
      MplCompilerMain.parseOptions("invalid");
    } catch (InvalidOptionException ex) {
      act = ex;
    }

    // then:
    assertThat(act).isNotNull();
    assertThat(act.getMessage())
        .startsWith("mpl: invalid compiler option INVALID; possible options are: ");
  }

  @Test
  public void test_parseOptions__with_multiple_options() throws Exception {
    // when:
    CompilerOptions options = MplCompilerMain.parseOptions("debug,transmitter");

    // then:
    assertThat(options.hasOption(DEBUG)).isTrue();
    assertThat(options.hasOption(TRANSMITTER)).isTrue();
    assertThat(options.getOptions()).containsOnly(DEBUG, TRANSMITTER);
  }

  @Test
  public void test_parseOptions__can_handle_minus() throws Exception {
    // when:
    CompilerOptions options = MplCompilerMain.parseOptions("delete-on-uninstall");

    // then:
    assertThat(options.hasOption(DELETE_ON_UNINSTALL)).isTrue();
    assertThat(options.getOptions()).containsOnly(DELETE_ON_UNINSTALL);
  }

}
