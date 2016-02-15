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
package de.adrodoc55.minecraft.mpl.antlr;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.MplConverter;
import de.adrodoc55.minecraft.mpl.antlr.commands.InvertingCommand;
import de.adrodoc55.minecraft.mpl.antlr.commands.NormalizingCommand;
import de.adrodoc55.minecraft.mpl.antlr.commands.ReferencingCommand;

public class IfBuffer {

  private final ChainBuffer origin;
  private final LinkedList<IfNestingLayer> stack = new LinkedList<>();
  // IfNestingLayer current;

  public IfBuffer(ChainBuffer chainBuffer) {
    origin = chainBuffer;
  }

  public ChainBuffer enterIf(boolean not, Command condition) {
    IfNestingLayer layer = new IfNestingLayer(not, condition);
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

  public static class LayerCompressor {
    public static CompressedLayerCommand compress(IfNestingLayer layer) {
      return new CompressedLayerCommand(layer);
    }

    public static LinkedList<Command> decompress(CompressedLayerCommand compressed) {
      return new LayerCompressor(compressed.layer).decompress();
    }

    private static LinkedList<Command> decompress(LayerCompressor parent,
        CompressedLayerCommand compressed) {
      return new LayerCompressor(parent, compressed.layer).decompress();
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

      Iterator<Command> thenIt = layer.getThenBlock().iterator();

      if (layer.getNot()) {
        add(thenIt, false);
        // addAsInverts(thenIt);
      } else {
        if (thenIt.hasNext()) { // First normal then does not need a reference
          Command firstThen = thenIt.next();
          firstThen = new Command(firstThen);
          firstThen.setConditional(true);
          layerCommands.add(firstThen);
        }
        add(thenIt, true);
        // addAsConditionals(thenIt);
      }

      // switching to else block
      inElse = true;

      Iterator<Command> elseIt = layer.getElseBlock().iterator();
      if (layer.getNot()) {
        add(elseIt, true);
        // addAsConditionals(elseIt);
      } else {
        add(elseIt, false);
        // addAsInverts(elseIt);
      }

      return layerCommands;
    }

    private void add(Iterator<Command> it, boolean dependOnSuccess) {
      boolean lastWasInverting = false;
      while (it.hasNext()) {
        Command command = it.next();

        LinkedList<Command> decompressed = null;
        if (command instanceof CompressedLayerCommand) {
          CompressedLayerCommand compressed = (CompressedLayerCommand) command;
          decompressed = LayerCompressor.decompress(this, compressed);
          command = decompressed.pop();
        }

        boolean isInverting = command instanceof InvertingCommand;
        if ((lastWasInverting || !command.isConditional()) && !isInverting) {
          boolean lastWasParentReference = false;
          if (!dependOnSuccess) {
            if (addParentReference(this)) {
              lastWasParentReference = true;
            }
          }

          int relative = getRelativeToCondition();
          Mode referencedMode;
          if (layer.needsNormalizer()) {
            // Normalizers are always chain commands.
            referencedMode = Mode.CHAIN;
          } else {
            referencedMode = layer.getCondition().getMode();
          }
          String blockId = MplConverter.toBlockId(referencedMode);
          ReferencingCommand reference = new ReferencingCommand(relative, blockId, dependOnSuccess);
          reference.setConditional(lastWasInverting || lastWasParentReference);
          layerCommands.add(reference);
          command = new Command(command);
          command.setConditional(true);
        }
        layerCommands.add(command);

        if (decompressed != null) {
          // Decompressed commands can't be added immediately, because the condition has to be added
          // first.
          layerCommands.addAll(decompressed);
        }

        lastWasInverting = isInverting;
      }
    }

    /**
     * Add's a reference to the parent of the given {@link LayerCompressor}. If the parent depends
     * on the grandparent's failure a reference to the grandparent is also added. This method is
     * recursive and will add parent references, until the root is reached or until a layer depends
     * on it's parent's success rather that failure.
     *
     * @param current
     * @return true if any reference was added.
     */
    private boolean addParentReference(LayerCompressor current) {
      LayerCompressor parent = current.parent;
      if (parent == null) {
        return false;
      }
      IfNestingLayer parentLayer = parent.layer;

      boolean dependOnParentFailure = parentLayer.not ^ parent.inElse;
      if (dependOnParentFailure) {
        // If a layer depends on it's parent's failure, it also needs a reference to it's
        // grandparent.
        // FIXME: conditionalität, siehe add()
        addParentReference(parent);
      }
      int relative = parent.getRelativeToCondition();
      relative -= layerCommands.size() + 1; // Plus 1 für eigene condition.
      Mode referencedMode;
      if (parentLayer.needsNormalizer()) {
        // Normalizers are always chain commands.
        referencedMode = Mode.CHAIN;
      } else {
        referencedMode = parentLayer.getCondition().getMode();
      }
      String blockId = MplConverter.toBlockId(referencedMode);
      ReferencingCommand reference =
          new ReferencingCommand(relative, blockId, !dependOnParentFailure);
      layerCommands.add(reference);
      return true;
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

  public static class CompressedLayerCommand extends Command {
    private final IfNestingLayer layer;

    public CompressedLayerCommand(IfNestingLayer layer) {
      super();
      this.layer = layer;
    }
    // TODO: throw UnsupportedOperationException für alle super methoden
  }

  public static class IfNestingLayer extends ChainBuffer {
    private final boolean not;
    private final Command condition;
    private final LinkedList<Command> thenBlock = new LinkedList<>();
    private boolean inElse = false;

    public IfNestingLayer(boolean not, Command condition) {
      this.not = not;
      this.condition = condition;
    }

    public boolean getNot() {
      return not;
    }

    Command getCondition() {
      return condition;
    }

    public boolean needsNormalizer() {
      if (not) {
        // Muss nicht iterieren, da der Erste nicht conditional sein kann und einmal nicht
        // conditional ausreicht.
        return !getElseBlock().isEmpty();
      } else {
        Iterator<Command> it = getThenBlock().iterator();
        if (it.hasNext()) {
          it.next(); // Ignore the first element.
        }
        while (it.hasNext()) {
          Command command = it.next();
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

    public List<Command> getThenBlock() {
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

    public List<Command> getElseBlock() {
      if (inElse) {
        return Collections.unmodifiableList(commands);
      } else {
        return new LinkedList<>();
      }
    }

  }
}
