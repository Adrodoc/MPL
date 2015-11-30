package de.adrodoc55.minecraft.mpl.chain_computing;

import java.io.File;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.CommandChain;
import de.adrodoc55.minecraft.mpl.Program;

public class AStarImplTest {

	public static void main(String[] args) {
		File turretsFile = new File("C:/Users/adrian/Programme/workspace/MplGenerator/src/main/resources/testdata.txt");
		File validateDirections = new File("C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_validateDirections.txt");
		File teleportation = new File("C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_teleportation.txt");
		File online = new File("C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_online.txt");
		File createPortal = new File("C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_createPortal.txt");
		Program program = new Program(createPortal);
		CommandChain commandChain = program.getChains().get(new Coordinate3D());
		new AStarChainComputer().computeOptimalChain(new Coordinate3D(), commandChain);
	}
	
}
