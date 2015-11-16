package de.adrodoc55.minecraft.mpl;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;

public class NoConditionalChainCalculator implements ChainCalculator {
	/*
	 * public static void main(String[] args) { File programFile = new File(
	 * "C:/Users/Adrian/Programme/workspace/MplGenerator/src/main/resources/testdata.txt"
	 * ); File outputFile = new File(programFile.getAbsolutePath() + ".py");
	 * Program p = new Program(programFile);
	 *
	 * List<Command> commands = p.getChains().values().iterator().next()
	 * .getCommands();
	 *
	 * JFrame frame = new JFrame("No conditional"); ChainRenderer
	 * optimalRenderer = new ChainRenderer(commands);
	 * frame.getContentPane().add(optimalRenderer, BorderLayout.CENTER);
	 * frame.pack(); frame.setLocationRelativeTo(null);
	 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 * frame.setVisible(true);
	 *
	 * NoConditionalChainCalculator calculator = new
	 * NoConditionalChainCalculator();
	 *
	 * CommandChain chain = new CommandChain(programFile.getName(), commands);
	 * CommandBlockChain optimal = calculator.calculateOptimalChain( new
	 * Coordinate3D(), chain); List<Coordinate3D> coordinates = Lists.transform(
	 * optimal.getCommandBlocks(), commandBlock -> { return
	 * commandBlock.getCoordinate(); }); optimalRenderer.render(coordinates);
	 *
	 * StringBuilder python = new StringBuilder(
	 * "from pymclevel.entity import TileEntity\n" +
	 * "from pymclevel.nbt import TAG_String\n" +
	 * "from pymclevel.nbt import TAG_Byte\n" + "\n" +
	 * "displayName = 'Generate " + programFile.getName() + "'\n" + "\n" +
	 * "def create_command_block(level, xyz, command, direction='south', mode='chain', conditional=False, auto=True):\n"
	 * + "    x, y, z = xyz\n" + "    		\n" +
	 * "    idDict = {'impulse' : 137, 'chain' : 211, 'repeat' : 210}\n" +
	 * "    blockId = idDict[mode]\n" +
	 * "    level.setBlockAt(x, y, z, blockId)\n" + "    \n" +
	 * "    damageList = ['down', 'up', 'north', 'south', 'west', 'east']\n" +
	 * "    damage = damageList.index(direction)\n" + "    if conditional:\n" +
	 * "        damage += 8\n" + "\n" +
	 * "    level.setBlockDataAt(x, y, z, damage)\n" +
	 * "    control = TileEntity.Create('Control', xyz)\n" +
	 * "    control['Command'] = TAG_String(command)\n" +
	 * "    control['auto'] = TAG_Byte(auto)\n" +
	 * "    level.addTileEntity(control)\n" + "\n" +
	 * "def perform(level, box, options):\n"); String indent = "    ";
	 *
	 * for (CommandBlock current : optimal.getCommandBlocks()) {
	 * python.append(indent + current.toPython() + "\n"); }
	 *
	 * outputFile.delete(); try { outputFile.getParentFile().mkdirs();
	 * outputFile.createNewFile(); } catch (IOException ex) { throw new
	 * RuntimeException(ex); } try (BufferedWriter writer = new
	 * BufferedWriter(new FileWriter( outputFile));) {
	 * writer.write(python.toString()); } catch (IOException ex) { throw new
	 * RuntimeException(ex); } }
	 */
	private boolean xAxisEnabled;
	private boolean yAxisEnabled;
	private boolean zAxisEnabled;

	public NoConditionalChainCalculator() {
		this(true, true, true);
	}

	public NoConditionalChainCalculator(boolean xAxisEnabled,
			boolean yAxisEnabled, boolean zAxisEnabled) {
		this.xAxisEnabled = xAxisEnabled;
		this.yAxisEnabled = yAxisEnabled;
		this.zAxisEnabled = zAxisEnabled;
	}

	public boolean isXAxisEnabled() {
		return xAxisEnabled;
	}

	public void enableXAxis(boolean xAxisEnabled) {
		this.xAxisEnabled = xAxisEnabled;
	}

	public boolean isYAxisEnabled() {
		return yAxisEnabled;
	}

	public void enableYAxis(boolean yAxisEnabled) {
		this.yAxisEnabled = yAxisEnabled;
	}

	public boolean isZAxisEnabled() {
		return zAxisEnabled;
	}

	public void enableZAxis(boolean zAxisEnabled) {
		this.zAxisEnabled = zAxisEnabled;
	}

	@Override
	public CommandBlockChain calculateOptimalChain(Coordinate3D start,
			CommandChain input) {
		List<Command> commands = input.getCommands();
		for (Command command : commands) {
			if (command.isConditional()) {
				throw new IllegalArgumentException(this.getClass()
						.getSimpleName()
						+ " can't handle Conditional Command: '"
						+ command.getCommand() + "'");
			}
		}
		int root;
		if (xAxisEnabled && yAxisEnabled && zAxisEnabled) {
			root = (int) Math.cbrt(commands.size());
		} else if (xAxisEnabled ^ yAxisEnabled ^ zAxisEnabled) {
			root = commands.size();
		} else if (!xAxisEnabled && !yAxisEnabled && !zAxisEnabled) {
			throw new IllegalStateException("At least one Axis must be enabled");
		} else {
			root = (int) Math.sqrt(commands.size());
		}
		root++;

		int count = 0;
		int zStart = start.getZ();
		int yStart = start.getY();
		int xStart = start.getX();
		int z = zStart;
		int y = yStart;
		int x = xStart;
		if (!zAxisEnabled)
			zStart -= root - 1;
		if (!yAxisEnabled)
			yStart -= root - 1;
		if (!xAxisEnabled)
			xStart -= root - 1;
		int zMod = 1;
		int yMod = 1;
		int xMod = 1;
		List<Coordinate3D> coordinates = new ArrayList<Coordinate3D>(
				commands.size() + 1);
		outer: for (; z < zStart + root; z += zMod) {
			for (; y >= 0 && y < yStart + root; y += yMod) {
				for (; x >= 0 && x < xStart + root; x += xMod) {
					if (count > commands.size() + 1) {
						break outer;
					}
					coordinates.add(new Coordinate3D(x, y, z));
					count++;
				}
				xMod *= -1;
				x += xMod;
			}
			yMod *= -1;
			y += yMod;
		}
		CommandBlockChain output = toCommandBlockChain(input, coordinates);
		return output;
	}
}
