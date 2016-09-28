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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.ITag;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagByteArray;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagInteger;
import com.evilco.mc.nbt.tag.TagList;
import com.evilco.mc.nbt.tag.TagShort;
import com.evilco.mc.nbt.tag.TagString;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.mpl.blocks.AirBlock;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.version.MplVersion;

/**
 * @author Adrodoc55
 */
public class SchematicConverter implements MplConverter {
  @Override
  public void write(MplCompilationResult result, String name, OutputStream out, MplVersion version)
      throws IOException {
    try (GZIPOutputStream zip = new GZIPOutputStream(out);
        NbtOutputStream nbt = new NbtOutputStream(zip);) {
      nbt.write(convert(result));
    }
  }

  public static TagCompound convert(MplCompilationResult result) {
    ImmutableMap<Coordinate3D, MplBlock> blockMap = result.getBlocks();
    ImmutableSet<Coordinate3D> coordinates = blockMap.keySet();
    Coordinate3D min = getMinCoordinate(coordinates);
    Coordinate3D max = getMaxCoordinate(coordinates);
    short width = (short) (1 + max.getX() - min.getX());
    short heigth = (short) (1 + max.getY() - min.getY());
    short length = (short) (1 + max.getZ() - min.getZ());
    int volume = width * heigth * length;
    ByteBuffer blocks = ByteBuffer.allocate(volume);
    ByteBuffer data = ByteBuffer.allocate(volume);
    List<ITag> tileEntities = new ArrayList<>(blockMap.size());
    for (int y = (int) min.getY(); y <= max.getY(); y++) {
      for (int z = (int) min.getZ(); z <= max.getZ(); z++) {
        for (int x = (int) min.getX(); x <= max.getX(); x++) {
          Coordinate3D coord = new Coordinate3D(x, y, z);
          MplBlock block = blockMap.get(coord);
          if (block == null) {
            block = new AirBlock(coord);
          }

          // block
          blocks.put(block.getByteBlockId());

          // data
          if (block instanceof CommandBlock) {
            data.put(((CommandBlock) block).getDamageValue());
          } else {
            data.put((byte) 0);
          }

          // tile entity
          if (block instanceof CommandBlock) {
            tileEntities.add(toControl((CommandBlock) block));
          }
        }
      }
    }
    TagCompound schematic = new TagCompound("Schematic");
    schematic.setTag(new TagShort("Width", width));
    schematic.setTag(new TagShort("Height", heigth));
    schematic.setTag(new TagShort("Length", length));
    schematic.setTag(new TagString("Materials", "Alpha"));
    schematic.setTag(new TagByteArray("Blocks", blocks.array()));
    schematic.setTag(new TagByteArray("Data", data.array()));
    schematic.setTag(new TagList("TileEntities", tileEntities));
    schematic.setTag(new TagList("Entities", new ArrayList<>()));
    schematic.setTag(new TagList("TileTicks", new ArrayList<>()));
    return schematic;
  }

  public static TagCompound toControl(CommandBlock block) {
    TagCompound control = new TagCompound("");
    control.setTag(new TagString("id", "Control"));
    control.setTag(new TagInteger("x", block.getX()));
    control.setTag(new TagInteger("y", block.getY()));
    control.setTag(new TagInteger("z", block.getZ()));
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
    // control.setTag(new TagByte("conditionMet", (byte) 0));
    // control.setTag(new TagInteger("SuccessCount", 0));
    // control.setTag(new TagString("LastOutput", ""));
    control.setTag(new TagByte("TrackOutput", (byte) 0));
    control.setTag(new TagByte("powered", (byte) 0));
    control.setTag(new TagByte("auto", (byte) (block.getNeedsRedstone() ? 0 : 1)));
    return control;
  }

}
