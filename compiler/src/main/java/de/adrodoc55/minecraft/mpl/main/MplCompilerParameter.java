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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;

/**
 * @author Adrodoc55
 */
public class MplCompilerParameter {
  private static final File STDOUT = new File("stdout");

  @Parameter(names = {"-h", "--help"}, help = true,
      description = "Print information about the commandline usage")
  private Boolean help;

  @Parameter(names = {"-c", "--option"}, converter = CompilerOptionConverter.class,
      description = "Specify compiler options; for instance: debug or transmitter")
  private List<CompilerOption> compilerOptions;

  @Parameter(names = {"-o", "--output"}, description = "Specify an output file")
  private File output = STDOUT;

  @Parameter(names = {"-t", "--type"}, description = "Specify the output type")
  private CompilationType type = CompilationType.STRUCTURE;

  @Parameter(names = {"-v", "--version"}, description = "Specify the target Minecraft version")
  private String version = MinecraftVersion.getDefault().toString();

  @Parameter(description = "<src-file>", required = true)
  private List<File> input;

  public boolean isHelp() {
    return help != null && help;
  }

  public CompilerOptions getCompilerOptions() {
    return new CompilerOptions(compilerOptions != null ? compilerOptions : Collections.emptyList());
  }

  public OutputStream getOutput() throws FileNotFoundException {
    return output == STDOUT ? System.out : new FileOutputStream(output);
  }

  public CompilationType getType() {
    return type;
  }

  public String getVersion() {
    return version;
  }

  public File getInput() throws ParameterException {
    if (input.size() != 1) {
      throw new ParameterException("Exactly one source file has to be specified");
    }
    return input.get(0).getAbsoluteFile();
  }
}
