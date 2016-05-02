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
package de.adrodoc55.minecraft.mpl.conversion;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.evilco.mc.nbt.tag.ITag;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagByteArray;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagInteger;
import com.evilco.mc.nbt.tag.TagList;
import com.evilco.mc.nbt.tag.TagShort;
import com.evilco.mc.nbt.tag.TagString;
import com.google.common.collect.ImmutableMap;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.mpl.blocks.AirBlock;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;

/**
 * @author Adrodoc55
 */
public class SchematicConverter extends MplConverter {

  public static TagCompound convert(MplCompilationResult result) {
    ImmutableMap<Coordinate3D, MplBlock> blockMap = result.getBlocks();
    Coordinate3D max = getMax(blockMap);
    int volume = max.getX() * max.getY() * max.getZ();
    ByteBuffer blocks = ByteBuffer.allocate(volume);
    ByteBuffer data = ByteBuffer.allocate(volume);
    List<ITag> tileEntities = new ArrayList<>(blockMap.size());
    for (int y = 0; y < max.getY(); y++) {
      for (int z = 0; z < max.getZ(); z++) {
        for (int x = 0; x < max.getX(); x++) {
          Coordinate3D coord = new Coordinate3D(x, y, z);
          MplBlock block = blockMap.get(coord);
          if (block == null) {
            block = new AirBlock(coord);
          }

          // block
          blocks.put(block.getByteBlockId());

          // data
          if (block instanceof CommandBlock) {
            data.put(toDamageValue((CommandBlock) block));
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
    schematic.setTag(new TagShort("Width", (short) max.getX()));
    schematic.setTag(new TagShort("Height", (short) max.getY()));
    schematic.setTag(new TagShort("Length", (short) max.getZ()));
    schematic.setTag(new TagString("Materials", "Alpha"));
    schematic.setTag(new TagByteArray("Blocks", blocks.array()));
    schematic.setTag(new TagByteArray("Data", data.array()));
    schematic.setTag(new TagList("TileEntities", tileEntities));
    schematic.setTag(new TagList("Entities", new ArrayList<>()));
    schematic.setTag(new TagList("TileTicks", new ArrayList<>()));
    return schematic;
  }

  private static Coordinate3D getMax(ImmutableMap<Coordinate3D, MplBlock> blockMap) {
    int x = 0, y = 0, z = 0;
    for (Coordinate3D coord : blockMap.keySet()) {
      x = Math.max(x, coord.getX());
      y = Math.max(y, coord.getY());
      z = Math.max(z, coord.getZ());
    }
    return new Coordinate3D(x + 1, y + 1, z + 1);
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
    control.setTag(new TagByte("auto", (byte) (block.needsRedstone() ? 0 : 1)));
    return control;
  }

}
