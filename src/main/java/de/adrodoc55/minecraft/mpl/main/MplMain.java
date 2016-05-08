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
package de.adrodoc55.minecraft.mpl.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagCompound;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.compilation.CompilationFailedException;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.compilation.MplCompiler;
import de.adrodoc55.minecraft.mpl.conversion.CommandConverter;
import de.adrodoc55.minecraft.mpl.conversion.PythonConverter;
import de.adrodoc55.minecraft.mpl.conversion.SchematicConverter;
import de.adrodoc55.minecraft.mpl.gui.MplFrame;
import de.adrodoc55.minecraft.mpl.gui.MplFramePM;

/**
 * @author Adrodoc55
 */
public class MplMain {
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      startGui();
      return;
    }
    try {
      startCompiler(args);
    } catch (CompilationFailedException ex) {
      System.err.println(ex.toString());
    } catch (InvalidOptionException ex) {
      System.err.println(ex.getLocalizedMessage());
    }
  }

  private static void startGui() throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    JDialog.setDefaultLookAndFeelDecorated(true);
    SwingUtilities.invokeLater(() -> {
      MplFrame frame = new MplFrame();
      MplFramePM pModel = new MplFramePM();
      frame.setPresentationModel(pModel);
      frame.setVisible(true);
    });
  }

  /**
   * -o | --output default=stdout<br>
   * -t | --type default=schematic |command|filter<br>
   * -c | --option :transmitter,debug<br>
   * Beispiel:<br>
   * java -jar MPL.jar -t command -o a.txt -c:debug a.mpl<br>
   * java -jar MPL.jar a.mpl -o a.schematic
   *
   *
   * file to compile
   *
   * @param args
   * @throws InvalidOptionException
   * @throws CompilationFailedException
   * @throws IOException
   * @throws Exception
   */
  @VisibleForTesting
  static void startCompiler(String[] args)
      throws InvalidOptionException, IOException, CompilationFailedException {
    String srcPath = null;
    OutputStream out = System.out;
    CompilationType type = CompilationType.SCHEMATIC;
    CompilerOptions options = new CompilerOptions();

    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      String argument;
      if (arg.startsWith("--")) {
        argument = arg.substring(2);
      } else if (arg.startsWith("-")) {
        argument = arg.substring(1);
      } else {
        srcPath = arg;
        continue;
      }
      switch (argument) {
        case "h":
        case "help":
          printHelp();
          return;
        case "c":
        case "option":
          if (i >= args.length) {
            throw new InvalidOptionException("mpl: missing argument for option " + argument);
          }
          options = parseOptions(args[++i]);
          continue;
        case "o":
        case "output":
          if (i >= args.length) {
            throw new InvalidOptionException("mpl: missing argument for option " + argument);
          }
          out = new FileOutputStream(args[++i]);
          continue;
        case "t":
        case "type":
          if (i >= args.length) {
            throw new InvalidOptionException("mpl: missing argument for option " + argument);
          }
          String typeString = args[++i];
          try {
            type = CompilationType.valueOf(typeString.toUpperCase());
          } catch (IllegalArgumentException ex) {
            throw new InvalidOptionException("mpl: invalid type " + typeString
                + "; possible types are " + Joiner.on(", ").join(CompilationType.values()));
          }
          continue;

        default:
          throw new InvalidOptionException(
              "mpl: invalid argument " + argument + " run with --help for help");
      }

    }
    if (srcPath == null) {
      throw new InvalidOptionException("You need to specify a source file");
    }
    File programFile = new File(srcPath).getAbsoluteFile();
    String name = FileUtils.getFilenameWithoutExtension(programFile);

    MplCompilationResult compiled = MplCompiler.compile(programFile, options);
    type.write(compiled, out, name);
  }

  private static void printHelp() {
    System.out.println("Usage: java -jar MPL.jar <options> <src-file>");
    System.out.println("where possible options include:");
    System.out.println("  -h | --help\t\t\t\t\tPrint information about the commandline usage");
    System.out.println(
        "  -c | --option <option1>[,<option2>...] \tSpecify compiler options; for instance: debug or transmitter");
    System.out
        .println("  -o | --output <path> \t\t\t\tSpecify an output file (defaults to stdout)");
    System.out.println(
        "  -t | --type schematic|command|filter \t\tSpecify the output type (defaults to schematic)");
  }

  private static CompilerOptions parseOptions(String string) throws InvalidOptionException {
    String[] split = string.split(",");
    CompilerOption[] options = new CompilerOption[split.length];
    for (int i = 0; i < split.length; i++) {
      try {
        options[i] = CompilerOption.valueOf(split[i].toUpperCase());
      } catch (IllegalArgumentException ex) {
        throw new InvalidOptionException("mpl: invalid compiler option " + split[i]
            + "; possible options are " + Joiner.on(", ").join(CompilerOption.values()));
      }
    }
    return new CompilerOptions(options);
  }

  private static enum CompilationType {
    SCHEMATIC {
      @Override
      public void write(MplCompilationResult compiled, OutputStream out, String name)
          throws IOException {
        TagCompound convert = SchematicConverter.convert(compiled);
        try (NbtOutputStream nbtOut = new NbtOutputStream(out);) {
          nbtOut.write(convert);
        }
      }
    },
    COMMAND {
      @Override
      public void write(MplCompilationResult compiled, OutputStream out, String name)
          throws IOException {
        List<String> convert = CommandConverter.convert(compiled);
        int i = 0;
        for (String string : convert) {
          out.write(("Command " + (++i) + ":\r\n").getBytes());
          out.write(string.getBytes());
        }
        out.close();
      }
    },
    FILTER {
      @Override
      public void write(MplCompilationResult compiled, OutputStream out, String name)
          throws IOException {
        String convert = PythonConverter.convert(compiled, name);
        out.write(convert.getBytes());
        out.close();
      }
    };
    public abstract void write(MplCompilationResult compiled, OutputStream out, String name)
        throws IOException;
  }

  private static class InvalidOptionException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidOptionException(String string) {
      super(string);
    }
  }
}
