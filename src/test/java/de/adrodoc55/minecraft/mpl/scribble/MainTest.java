package de.adrodoc55.minecraft.mpl.scribble;

import java.io.File;
import java.io.IOException;

import de.adrodoc55.minecraft.mpl.Main;
import de.adrodoc55.minecraft.mpl.antlr.CompilationFailedException;

public class MainTest {

  public static void main(String[] args) throws IOException, CompilationFailedException {
    File turretsFile = new File(
        "C:/Users/adrian/Programme/workspace/MplGenerator/src/main/resources/testdata.txt");
    File validateDirections = new File(
        "C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_validateDirections.txt");
    File teleportation = new File(
        "C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_teleportation.txt");
    File online = new File(
        "C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_online.txt");
    File createPortal = new File(
        "C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_createPortal.txt");

    File acv = new File(
        "C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ApertureCraft Vanilla.txt");
    File test = new File("C:/Users/adrian/Programme/workspace/MPL/src/test/resources/mpl-test.txt");
    File inputFile = test;
    File outputFile =
        new File("C:/Users/Adrian/Documents/MCEdit/Filters", inputFile.getName() + ".py");
    Main.main(inputFile, outputFile);
  }

}
