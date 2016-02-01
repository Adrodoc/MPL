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
