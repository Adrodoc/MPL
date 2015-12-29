package de.adrodoc55.minecraft.mpl.scribble;

import java.io.File;
import java.io.IOException;

import de.adrodoc55.minecraft.mpl.MplCompiler;

public class Scribble {

    public static void main(String[] args) throws IOException {
        File programFile = new File(
                "C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_antiBridges.txt");
        MplCompiler.compile(programFile);
    }

}
