package de.adrodoc55.minecraft.mpl.placement;

import java.util.List;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.kussm.direction.Directions;

public class MplDebugProjectPlacer extends MplChainPlacer {

  private Coordinate3D start = new Coordinate3D().plus(4, getOrientation().getC().getAxis());

  public MplDebugProjectPlacer(ChainContainer container, CompilerOptions options) {
    super(container, options);
  }

  @Override
  public List<CommandBlockChain> place() {
    for (CommandChain chain : container.getChains()) {
      addChain(chain);
    }
    addUnInstallation();
    return chains;
  }

  public void addChain(CommandChain chain) {
    Coordinate3D max = new Coordinate3D(1, 1, 1).plus(chain.getCommands().size() + 1,
        getOrientation().getA().getAxis());
    Directions template = newDirectionsTemplate(max, getOrientation());

    chain.getCommands().add(0, new MplSkip(false /* First TRANSMITTER can be referenced */));
    CommandBlockChain result = generateFlat(chain, start, template);
    chains.add(result);
    start = start.plus(getOrientation().getC().toCoordinate().mult(2));
  }

  protected void addUnInstallation() {
    start = new Coordinate3D();
    List<ChainLink> installation = container.getInstall().getCommands();
    List<ChainLink> uninstallation = container.getUninstall().getCommands();

    for (CommandBlockChain chain : chains) {
      Coordinate3D chainStart = chain.getBlocks().get(0).getCoordinate();
      // TODO: Alle ArmorStands taggen, damit nur ein uninstallation command notwendig
      installation.add(0,
          new Command("/summon ArmorStand ${origin + (" + chainStart.toAbsoluteString()
              + ")} {CustomName:" + chain.getName()
              + ",NoGravity:1,Invisible:1,Invulnerable:1,Marker:1,CustomNameVisible:1}"));
      uninstallation.add(new Command("/kill @e[type=ArmorStand,name=" + chain.getName() + "]"));
      // uninstallation
      // .add(0,new Command("/kill @e[type=ArmorStand,name=" + name + "_NOTIFY]"));
      // uninstallation
      // .add(0,new Command("/kill @e[type=ArmorStand,name=" + name + "_INTERCEPTED]"));
    }

    if (!installation.isEmpty()) {
      installation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
    }
    if (!uninstallation.isEmpty()) {
      uninstallation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
    }
    generateUnInstallation();
  }

  @Override
  protected void generateUnInstallation() {
    addChain(container.getInstall());
    addChain(container.getUninstall());
  }

  @Override
  protected Coordinate3D getOptimalSize() {
    throw new UnsupportedOperationException();
  }

}
