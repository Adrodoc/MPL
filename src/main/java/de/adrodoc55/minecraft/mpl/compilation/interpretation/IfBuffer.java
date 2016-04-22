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
package de.adrodoc55.minecraft.mpl.compilation.interpretation;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.adrodoc55.minecraft.mpl.commands.Command;
import de.adrodoc55.minecraft.mpl.commands.InvertingCommand;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.NormalizingCommand;
import de.adrodoc55.minecraft.mpl.commands.ReferencingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.conversion.MplConverter;

/**
 * @author Adrodoc55
 */
class IfBuffer {
  private final ChainBuffer origin;
  private final LinkedList<IfNestingLayer> stack = new LinkedList<>();

  public IfBuffer(ChainBuffer chainBuffer) {
    origin = chainBuffer;
  }

  public ChainBuffer enterIf(boolean not, Command condition) {
    IfNestingLayer layer = new IfNestingLayer(not, condition, origin);
    stack.push(layer);
    return layer;
  }

  public void switchToElseBlock() {
    stack.peek().switchToElseBlock();
  }

  public ChainBuffer exitIf() {
    CompressedLayerCommand compressed = LayerCompressor.compress(stack.pop());
    IfNestingLayer parent = stack.peek();
    if (parent == null) {
      LinkedList<Command> decompressed = LayerCompressor.decompress(compressed);
      for (Command command : decompressed) {
        origin.add(command);
      }
      return origin;
    } else {
      parent.add(compressed);
      return parent;
    }
  }
}



/**
 * @author Adrodoc55
 */
class LayerCompressor {
  public static CompressedLayerCommand compress(IfNestingLayer layer) {
    return new CompressedLayerCommand(layer);
  }

  public static LinkedList<Command> decompress(CompressedLayerCommand compressed) {
    return new LayerCompressor(compressed.getLayer()).decompress();
  }

  private static LinkedList<Command> decompress(LayerCompressor parent,
      CompressedLayerCommand compressed) {
    return new LayerCompressor(parent, compressed.getLayer()).decompress();
  }

  private final LayerCompressor parent;
  private final IfNestingLayer layer;
  private final LinkedList<Command> layerCommands = new LinkedList<>();

  private boolean inElse = false;

  private LayerCompressor(IfNestingLayer layer) {
    this(null, layer);
  }

  private LayerCompressor(LayerCompressor parent, IfNestingLayer layer) {
    this.parent = parent;
    this.layer = layer;
  }

  private LinkedList<Command> decompress() {
    layerCommands.add(layer.getCondition());
    boolean normalizer = layer.needsNormalizer();
    if (normalizer) {
      layerCommands.add(new NormalizingCommand());
    }

    Iterator<ChainPart> thenIt = layer.getThenBlock().iterator();

    if (layer.isNot()) {
      add(thenIt, false);
    } else {
      if (thenIt.hasNext()) { // First normal then does not need a reference
        ChainPart firstThen = thenIt.next();
        if (!(firstThen instanceof Command)) {
          // TODO: Besseren Fehler werfen
          throw new IllegalStateException("Skip is not allowed within if body!");
        }
        Command firstThenCommand = new Command((Command) firstThen);
        firstThenCommand.setConditional(true);
        layerCommands.add(firstThenCommand);
      }
      add(thenIt, true);
    }

    // switching to else block
    inElse = true;

    Iterator<ChainPart> elseIt = layer.getElseBlock().iterator();
    if (layer.isNot()) {
      add(elseIt, true);
    } else {
      add(elseIt, false);
    }

    return layerCommands;
  }

  private void add(Iterator<ChainPart> it, boolean dependOnSuccess) {
    boolean lastWasInverting = false;
    while (it.hasNext()) {
      ChainPart chainPart = it.next();
      if (!(chainPart instanceof Command)) {
        // TODO: Besseren Fehler werfen
        throw new IllegalStateException("Skip is not allowed within if body!");
      }
      Command command = (Command) chainPart;

      CompressedLayerCommand compressed = null;
      if (command instanceof CompressedLayerCommand) {
        compressed = (CompressedLayerCommand) command;
        command = compressed.getLayer().getCondition();
      }

      boolean isInverting = command instanceof InvertingCommand;
      if ((lastWasInverting || !command.isConditional()) && !isInverting) {
        addConditionReferences(lastWasInverting);
        command = new Command(command);
        command.setConditional(true);
      }
      layerCommands.add(command);

      if (compressed != null) {
        LinkedList<Command> decompressed = LayerCompressor.decompress(this, compressed);
        decompressed.pop(); // The condition has already been added
        layerCommands.addAll(decompressed);
      }

      lastWasInverting = isInverting;
    }
  }

  /**
   * Add's a reference to the parent of the given {@link LayerCompressor}. If the parent depends on
   * the grandparent's failure a reference to the grandparent is also added. This method is
   * recursive and will add parent references, until the root is reached or until a layer depends on
   * it's parent's success rather that failure.
   *
   * @param current
   * @return true if any reference was added.
   */
  private void addConditionReferences(boolean makeFirstConditional) {
    LinkedList<LayerCompressor> stack = new LinkedList<>();

    LayerCompressor current = this;
    while (current != null) {
      stack.push(current);
      // If a layer depends on it's parent's failure, it also needs a reference to it's
      // grandparent.
      boolean dependsOnFailure = dependsOnFailure(current);
      if (!dependsOnFailure) {
        break;
      }
      current = current.parent;
    }

    boolean first = !makeFirstConditional;
    while (!stack.isEmpty()) {
      current = stack.pop();
      IfNestingLayer layer = current.layer;

      int relative = current.getRelativeToCondition();
      for (LayerCompressor child : stack) {
        // Minus 1, da die current condition bereits im parent (in der Methode add) geadded wurde.
        relative -= child.layerCommands.size() - 1;
      }
      Mode referencedMode;
      if (layer.needsNormalizer()) {
        // Normalizers are always chain commands.
        referencedMode = Mode.CHAIN;
      } else {
        referencedMode = layer.getCondition().getMode();
      }
      String blockId = MplConverter.toBlockId(referencedMode);
      boolean dependsOnFailure = dependsOnFailure(current);
      ReferencingCommand reference = new ReferencingCommand(relative, blockId, !dependsOnFailure);
      reference.setConditional(!first);
      layerCommands.add(reference);

      first = false;
    }
  }

  /**
   * Return's whether the current context requires the current condition to fail.
   *
   * @param current
   * @return
   */
  private boolean dependsOnFailure(LayerCompressor current) {
    boolean dependsOnFailure = current.layer.isNot() ^ current.inElse;
    return dependsOnFailure;
  }

  private int getRelativeToCondition() {
    if (layer.needsNormalizer()) {
      // Minus 1, da normalizer an 2ter Stelle.
      return -(layerCommands.size() - 1);
    } else {
      return -layerCommands.size();
    }
  }
}



/**
 * @author Adrodoc55
 */
class CompressedLayerCommand extends Command {
  private final IfNestingLayer layer;

  public CompressedLayerCommand(IfNestingLayer layer) {
    super((String) null);
    this.layer = layer;
  }

  public IfNestingLayer getLayer() {
    return layer;
  }
}



/**
 * @author Adrodoc55
 */
class IfNestingLayer extends ChainBuffer {
  private final boolean not;
  private final Command condition;
  private final LinkedList<ChainPart> thenBlock = new LinkedList<>();
  private boolean inElse = false;

  private ChainBuffer origin;

  public IfNestingLayer(boolean not, Command condition, ChainBuffer origin) {
    this.not = not;
    this.condition = condition;
    this.origin = origin;
  }

  public boolean isNot() {
    return not;
  }

  public Command getCondition() {
    return condition;
  }

  public boolean needsNormalizer() {
    if (not) {
      // Muss nicht iterieren, da der Erste nicht conditional sein kann und einmal nicht
      // conditional ausreicht.
      return !getElseBlock().isEmpty();
    } else {
      Iterator<ChainPart> it = getThenBlock().iterator();
      if (it.hasNext()) {
        it.next(); // Ignore the first element.
      }
      while (it.hasNext()) {
        ChainPart chainPart = it.next();
        if (!(chainPart instanceof Command)) {
          // TODO: Besseren Fehler werfen
          throw new IllegalStateException("Skip is not allowed within if body!");
        }
        Command command = (Command) chainPart;
        if (!command.isConditional()) {
          return true;
        }
      }
      return false;
    }
  }

  public void switchToElseBlock() {
    thenBlock.addAll(commands);
    commands.clear();
    inElse = true;
  }

  public List<ChainPart> getThenBlock() {
    if (!inElse) {
      // If we are still editing the then block and the thenBlock is not already refreshed,
      // refresh the elements.
      if (!thenBlock.equals(commands)) {
        thenBlock.clear();
        thenBlock.addAll(commands);
      }
    }
    return Collections.unmodifiableList(thenBlock);
  }

  public List<ChainPart> getElseBlock() {
    if (inElse) {
      return Collections.unmodifiableList(commands);
    } else {
      return new LinkedList<>();
    }
  }



  public String getName() throws IllegalStateException {
    return origin.getName();
  }

  public boolean isInstall() {
    return origin.isInstall();
  }

  public boolean isUninstall() {
    return origin.isUninstall();
  }

  public boolean isProcess() {
    return origin.isProcess();
  }

  public boolean isScript() {
    return origin.isScript();
  }

  public boolean isRepeatingProcess() {
    return origin.isRepeatingProcess();
  }

  public boolean isRepeatingContext() {
    return origin.isRepeatingContext();
  }
}
