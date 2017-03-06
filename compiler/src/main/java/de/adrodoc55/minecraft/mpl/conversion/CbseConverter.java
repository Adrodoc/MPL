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

import static com.google.common.base.Charsets.UTF_8;
import static de.adrodoc55.minecraft.coordinate.Axis3D.X;
import static de.adrodoc55.minecraft.coordinate.Axis3D.Y;
import static de.adrodoc55.minecraft.coordinate.Axis3D.Z;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;

/**
 * @author Adrodoc55
 */
public class CbseConverter implements MplConverter {
  @Override
  public void write(MplCompilationResult result, String name, OutputStream out,
      MinecraftVersion version) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, UTF_8))) {
      writer.write(convert(result));
    }
  }

  public static String convert(MplCompilationResult result) {
    StringBuilder sb = new StringBuilder();
    Orientation3D orientation = result.getOrientation();
    int xOffset = orientation.get(X).isNegative() ? -1 : 1;
    int yOffset = orientation.get(Y).isNegative() ? -1 : 1;
    int zOffset = orientation.get(Z).isNegative() ? -1 : 1;
    sb.append("xOffset:").append(xOffset).append('\n');
    sb.append("yOffset:").append(yOffset).append('\n');
    sb.append("zOffset:").append(zOffset).append('\n');
    sb.append("#cb\n");
    for (MplBlock block : result.getBlocks().values()) {
      if (block instanceof CommandBlock) {
        sb.append(convertBlock((CommandBlock) block));
      }
    }
    return sb.toString();
  }

  private static CharSequence convertBlock(CommandBlock block) {
    StringBuilder sb = new StringBuilder();
    Coordinate3D coordinate = block.getCoordinate();
    int x = (int) coordinate.getX();
    int y = (int) coordinate.getY();
    int z = (int) coordinate.getZ();
    sb.append(x).append(',').append(y).append(',').append(z).append('\n');

    sb.append(block.getMode()).append('|');
    sb.append(block.getDirection()).append('|');
    sb.append(block.isConditional() ? "conditional" : "unconditional").append('|');
    sb.append(block.getNeedsRedstone() ? "redstone" : "active").append('\n');

    sb.append("cmd:").append(block.getCommand()).append('\n');

    sb.append('-').append('\n');
    return sb;
  }
}
