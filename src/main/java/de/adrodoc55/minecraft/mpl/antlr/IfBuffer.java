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
import de.adrodoc55.minecraft.mpl.antlr.commands.NormalizingCommand;
import de.adrodoc55.minecraft.mpl.antlr.commands.ReferencingCommand;

public class IfBuffer {

  private final ChainBuffer origin;
  // private final LinkedList<IfNestingLayer> stack = new LinkedList<>();
  IfNestingLayer current;

  public IfBuffer(ChainBuffer chainBuffer) {
    origin = chainBuffer;
  }

  public ChainBuffer enterIf(boolean not, Command condition) {
    current = new IfNestingLayer(current, not, condition);
    return current;
  }

  public void switchToElseBlock() {
    current.switchToElseBlock();
  }

  public ChainBuffer exitIf() {
    CompressedLayerCommand compressed = LayerCompressor.compress(current);
    current = current.parent; // pop
    if (current == null) {
      LinkedList<Command> decompressed = decompress(compressed);
      for (Command command : decompressed) {
        origin.add(command);
      }
      return origin;
    } else {
      current.add(compressed);
      return current;
    }
  }

  public static class LayerCompressor {
    private static LinkedList<Command> decompress(CompressedLayerCommand compressed) {
      LinkedList<Command> result = new LinkedList<>();
      result.add(new Command(compressed));
      LinkedList<Command> layerCommands = compressed.getLayerCommands();
      result.addAll(layerCommands);
      return result;
    }

    public static CompressedLayerCommand compress(IfNestingLayer layer) {
      return new LayerCompressor(layer).compress();
    }

    private final IfNestingLayer layer;
    LinkedList<Command> layerCommands = new LinkedList<>();

    public LayerCompressor(IfNestingLayer layer) {
      this.layer = layer;
    }

    /**
     * Materializes and compresses the current NestingLayer into a {@link CompressedLayerCommand}.
     *
     * @return compressed
     */
    private CompressedLayerCommand compress() {
      boolean not = layer.getNot();
      Command condition = layer.getCondition();

      boolean normalizer = layer.needsNormalizer();
      if (normalizer) {
        layerCommands.add(new NormalizingCommand());
      }
      List<Command> thenBlock = layer.getThenBlock();
      Iterator<Command> thenIt = thenBlock.iterator();

      if (!not) {
        if (thenIt.hasNext()) { // First normal then does not need a reference
          Command firstThen = thenIt.next();
          firstThen.setConditional(true);
          layerCommands.add(firstThen);
        }
        addAsConditionals(thenIt, condition.getMode(), normalizer);
      }
      CompressedLayerCommand compressed = new CompressedLayerCommand(condition, layerCommands);
      return compressed;
    }

    private void addAsConditionals(Iterator<Command> it, Mode conditionMode, boolean normalizer) {
      // TODO: normalizer müsste an dieser Stelle immer true sein.
      while (it.hasNext()) {
        Command command = it.next();
        if (!command.isConditional()) {
          int relative;
          Mode referencedMode;
          if (normalizer) {
            relative = -layerCommands.size();
            referencedMode = layerCommands.get(0).getMode();
          } else {
            relative = -(layerCommands.size() + 1);
            referencedMode = conditionMode;
          }
          String blockId = MplConverter.toBlockId(referencedMode);
          ReferencingCommand reference = new ReferencingCommand(relative, blockId, true);
          layerCommands.add(reference);
          command.setConditional(true);
        }
        layerCommands.add(command);
      }
    }

    private void addAsInverts(Iterator<Command> it, Mode conditionMode, boolean normalizer) {
      while (it.hasNext()) {
        Command command = it.next();
        if (!command.isConditional()) {
          addParentReference(layer);
          int relative;
          Mode referencedMode;
          if (normalizer) {
            relative = -layerCommands.size();
            referencedMode = layerCommands.get(0).getMode();
          } else {
            relative = -(layerCommands.size() + 1);
            referencedMode = conditionMode;
          }
          String blockId = MplConverter.toBlockId(referencedMode);
          ReferencingCommand reference = new ReferencingCommand(relative, blockId, false);
          layerCommands.add(reference);
          command.setConditional(true);
        }
        layerCommands.add(command);
      }
    }

    private void addParentReference(IfNestingLayer layer) {
      IfNestingLayer parent = layer.parent;
      if (parent == null) {
        return;
      }
      boolean dependOnParentFailure = parent.not ^ parent.inElse;
      if (dependOnParentFailure) {
        // If a layer depends on it's parent's failure, it also needs a reference to it's
        // grandparent.
        addParentReference(parent.parent);

        int relative;
        Mode referencedMode;
        if (normalizer) {
          relative = -layerCommands.size();
          referencedMode = layerCommands.get(0).getMode();
        } else {
          relative = -(layerCommands.size() + 1);
          referencedMode = conditionMode;
        }
        String blockId = MplConverter.toBlockId(referencedMode);
        ReferencingCommand reference = new ReferencingCommand(relative, blockId, false);
      } else {

      }

    }

  }

  public static class CompressedLayerCommand extends Command {
    private final LinkedList<Command> layerCommands;

    public CompressedLayerCommand(Command condition, LinkedList<Command> layerCommands) {
      super(condition.getCommand());
      this.layerCommands = layerCommands;
    }

    public LinkedList<Command> getLayerCommands() {
      return layerCommands;
    }
  }

  public static class IfNestingLayer extends ChainBuffer {
    private final IfNestingLayer parent;
    private final boolean not;
    private final Command condition;
    private final LinkedList<Command> thenBlock = new LinkedList<>();
    private boolean inElse = false;

    public IfNestingLayer(IfNestingLayer parent, boolean not, Command condition) {
      this.parent = parent;
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
        return !getElseBlock().isEmpty();
      } else {
        return getThenBlock().size() > 1;
      }
    }

    public void switchToElseBlock() {
      thenBlock.addAll(commands);
      commands.clear();
      inElse = true;
    }

    public List<Command> getThenBlock() {
      if (!inElse) {
        // If we are still editing the then block, refresh the elements.
        thenBlock.clear();
        thenBlock.addAll(commands);
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
