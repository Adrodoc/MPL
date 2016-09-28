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
package de.adrodoc55.minecraft.mpl.version;

import java.util.Collections;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.adrodoc55.commons.Comparables;
import de.adrodoc55.commons.Version;

/**
 * @author Adrodoc55
 */
public enum MinecraftVersion implements Comparables<MinecraftVersion> {
  /**
   * 15w35a (1.9)
   */
  _15w35a("1.9") {
    @Override
    public String markerEntity() {
      return "ArmorStand";
    }

    @Override
    public String commandBlockMinecart() {
      return "MinecartCommandBlock";
    }

    @Override
    public String fallingBlock() {
      return "FallingSand";
    }
  },
  /**
   * 16w32a (1.11)
   */
  _16w32a("1.11") {
    @Override
    public String markerEntity() {
      return "armor_stand";
    }

    @Override
    public String commandBlockMinecart() {
      return "commandblock_minecart";
    }

    @Override
    public String fallingBlock() {
      return "falling_block";
    }
  };
  private static final ImmutableList<MinecraftVersion> VALUES =
      ImmutableList.copyOf(MinecraftVersion.values());

  public static ImmutableList<MinecraftVersion> getValues() {
    return VALUES;
  }

  public static MinecraftVersion getDefault() {
    return Collections.max(VALUES);
  }

  public static MinecraftVersion getVersion(String version) {
    boolean snapshot = isSnapshotVersion(version);
    Version other = new Version(version);
    for (MinecraftVersion mv : VALUES.reverse()) {
      Version v = new Version(snapshot ? mv.getSnapshotVersion() : mv.getMajorVersion());
      if (v.isLessThanOrEqualTo(other)) {
        return mv;
      }
    }
    return Collections.min(VALUES);
  }

  public static boolean isSnapshotVersion(String version) {
    return version.contains("w");
  }

  private final @Nullable String version;

  private MinecraftVersion(@Nullable String snapshotVersion) {
    this.version = snapshotVersion;
  }

  /**
   * Returns the snapshot version that first introduced the change that is reflected by this
   * {@link MinecraftVersion}.
   *
   * @return the snapshot version
   */
  public String getSnapshotVersion() {
    return super.toString().substring(1);
  }

  /**
   * Returns the official release version that is reflected by this {@link MinecraftVersion}.
   *
   * @return the official release version
   */
  public @Nullable String getMajorVersion() {
    return version;
  }

  @Override
  public String toString() {
    String result = getSnapshotVersion();
    String majorVersion = getMajorVersion();
    if (majorVersion != null) {
      result += " (" + majorVersion + ")";
    }
    return result;
  }

  public abstract String markerEntity();

  public abstract String commandBlockMinecart();

  public abstract String fallingBlock();
}
