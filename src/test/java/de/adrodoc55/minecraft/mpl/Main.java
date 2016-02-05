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
package de.adrodoc55.minecraft.mpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.antlr.CompilationFailedException;

public class Main {

  public static void main(String[] args) throws IOException, CompilationFailedException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Expected exactly two Arguments: imputFile, outputDir");
    }
    File inputFile = new File(args[0]);
    File outputDir = new File(args[1]);
    File outputFile = new File(outputDir, inputFile.getName() + ".py");
    main(inputFile, outputFile);
  }

  public static void main(File inputFile, File outputFile)
      throws IOException, CompilationFailedException {
    List<CommandBlockChain> chains = MplCompiler.compile(inputFile);
    String name = FileUtils.getFilenameWithoutExtension(inputFile);
    String python = PythonConverter.convert(chains, name);

    outputFile.getParentFile().mkdirs();
    outputFile.createNewFile();
    try (BufferedWriter writer = Files.newBufferedWriter(outputFile.toPath());) {
      writer.write(python);
    }
  }

}
