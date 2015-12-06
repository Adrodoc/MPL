package de.adrodoc55.scribble;

import java.io.File;
import java.io.IOException;

import de.adrodoc55.antlr.mpl.MplCompiler;
import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.CommandChain;
import de.adrodoc55.minecraft.mpl.Program;
import de.adrodoc55.minecraft.mpl.chain_computing.AStarChainComputer;

public class AStarImplScribbleTest {

    public static void main(String[] args) throws IOException {
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
        Program program = MplCompiler.compile(createPortal);
        CommandChain commandChain = program.getChains().get(new Coordinate3D());
        new AStarChainComputer().computeOptimalChain(new Coordinate3D(),
                commandChain);
    }

}
