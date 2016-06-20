/*
 * Minecraft Programming Language (MPL): A language for easy development of command block
 * applications including an IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL.
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
 * Minecraft Programming Language (MPL): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, inklusive einer IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL.
 *
 * MPL ist freie Software: Sie können diese unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.conversion;

import static de.adrodoc55.minecraft.mpl.MplUtils.getMaxCoordinate;
import static de.adrodoc55.minecraft.mpl.MplUtils.getMinCoordinate;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.ITag;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagInteger;
import com.evilco.mc.nbt.tag.TagList;
import com.evilco.mc.nbt.tag.TagString;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.mpl.blocks.AirBlock;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Adrodoc55
 */
public class StructureConverter implements MplConverter {
  private final List<State> states = new ArrayList<>();

  @Override
  public void write(MplCompilationResult result, String name, OutputStream out) throws IOException {
    try (GZIPOutputStream zip = new GZIPOutputStream(out);
        NbtOutputStream nbt = new NbtOutputStream(zip);) {
      nbt.write(convert(result));
    }
  }

  public TagCompound convert(MplCompilationResult result) {
    states.clear();
    ImmutableMap<Coordinate3D, MplBlock> blockMap = result.getBlocks();
    ImmutableSet<Coordinate3D> coordinates = blockMap.keySet();
    Coordinate3D min = getMinCoordinate(coordinates);
    Coordinate3D max = getMaxCoordinate(coordinates);

    List<ITag> blocks = new ArrayList<>();
    for (int y = min.getY(); y <= max.getY(); y++) {
      for (int z = min.getZ(); z <= max.getZ(); z++) {
        for (int x = min.getX(); x <= max.getX(); x++) {
          Coordinate3D coord = new Coordinate3D(x, y, z);
          MplBlock block = blockMap.get(coord);
          if (block == null) {
            block = new AirBlock(coord);
          }

          TagCompound tag = new TagCompound("");
          tag.setTag(new TagInteger("state", registerState(block)));
          tag.setTag(new TagList("pos", posAt(x, y, z)));

          if (block instanceof CommandBlock) {
            TagCompound nbt = toControl((CommandBlock) block);
            nbt.setName("nbt");
            tag.setTag(nbt);
          }
          blocks.add(tag);
        }
      }
    }
    short sizeX = (short) (1 + max.getX() - min.getX());
    short sizeY = (short) (1 + max.getY() - min.getY());
    short sizeZ = (short) (1 + max.getZ() - min.getZ());
    List<ITag> palette = states.stream().map(s -> s.getState()).collect(toList());

    TagCompound structure = new TagCompound("");
    structure.setTag(new TagInteger("version", 1));
    structure.setTag(new TagString("author", "MPL"));
    structure.setTag(new TagList("blocks", blocks));
    structure.setTag(new TagList("entities", new ArrayList<>()));
    structure.setTag(new TagList("palette", palette));
    structure.setTag(new TagList("size", posAt(sizeX, sizeY, sizeZ)));

    return structure;
  }

  protected int registerState(MplBlock block) {
    String blockId = "minecraft:" + block.getStringBlockId();
    String conditional = null;
    String facing = null;
    if (block instanceof CommandBlock) {
      CommandBlock cmd = (CommandBlock) block;
      conditional = String.valueOf(cmd.isConditional());
      facing = cmd.getDirection().name().toLowerCase(Locale.US);
    }

    State newState = new State(blockId, conditional, facing);
    int idx = states.indexOf(newState);
    if (idx >= 0)
      return idx;

    newState.setState(createNbtState(blockId, conditional, facing));
    states.add(newState);
    return states.size() - 1;
  }

  public static TagCompound createNbtState(String blockId, String conditional, String facing) {
    TagCompound state = new TagCompound("");
    state.setTag(new TagString("Name", blockId));
    if (conditional != null || facing != null) {
      TagCompound properties = new TagCompound("Properties");
      properties.setTag(new TagString("conditional", conditional));
      properties.setTag(new TagString("facing", facing));
      state.setTag(properties);
    }
    return state;
  }

  @EqualsAndHashCode(exclude = "state")
  @RequiredArgsConstructor
  @Getter
  @Setter
  private static class State {
    private final String blockId;
    private final String conditional;
    private final String facing;
    private TagCompound state;
  }

  public static List<ITag> posAt(int sizeX, int sizeY, int sizeZ) {
    List<ITag> size = new ArrayList<>(3);
    size.add(new TagInteger("", sizeX));
    size.add(new TagInteger("", sizeY));
    size.add(new TagInteger("", sizeZ));
    return size;
  }

  public static TagCompound toControl(CommandBlock block) {
    TagCompound control = new TagCompound("");
    control.setTag(new TagString("id", "Control"));
    // control.setTag(new TagInteger("x", block.getX()));
    // control.setTag(new TagInteger("y", block.getY()));
    // control.setTag(new TagInteger("z", block.getZ()));
    control.setTag(new TagString("CustomName", "@"));
    // TagCompound commandStats = new TagCompound("CommandStats");
    // commandStats.setTag(new TagString("SuccessCountName", ""));
    // commandStats.setTag(new TagString("SuccessCountObjective", ""));
    // commandStats.setTag(new TagString("AffectedBlocksName", ""));
    // commandStats.setTag(new TagString("AffectedBlocksObjective", ""));
    // commandStats.setTag(new TagString("AffectedEntitiesName", ""));
    // commandStats.setTag(new TagString("AffectedEntitiesObjective", ""));
    // commandStats.setTag(new TagString("AffectedItemsName", ""));
    // commandStats.setTag(new TagString("AffectedItemsObjective", ""));
    // commandStats.setTag(new TagString("QueryResultName", ""));
    // commandStats.setTag(new TagString("QueryResultObjective", ""));
    // control.setTag(commandStats);
    control.setTag(new TagString("Command", block.getCommand()));
    control.setTag(new TagByte("conditionMet", (byte) 0));
    control.setTag(new TagInteger("SuccessCount", 0));
    // control.setTag(new TagString("LastOutput", ""));
    control.setTag(new TagByte("TrackOutput", (byte) 0));
    control.setTag(new TagByte("powered", (byte) 0));
    control.setTag(new TagByte("auto", (byte) (block.getNeedsRedstone() ? 0 : 1)));
    return control;
  }

}
