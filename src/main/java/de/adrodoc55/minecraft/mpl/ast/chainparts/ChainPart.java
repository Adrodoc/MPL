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
package de.adrodoc55.minecraft.mpl.ast.chainparts;

import de.adrodoc55.commons.Named;
import de.adrodoc55.minecraft.mpl.ast.MplNode;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.interpretation.IllegalModifierException;

/**
 * @author Adrodoc55
 */
public interface ChainPart extends MplNode, Named, Dependable {
  /**
   * Set the {@link Mode} of this {@link ChainPart} (optional operation).
   * <p>
   * Setting the {@link Mode} of this {@link ChainPart} does not necessarily affect the output of
   * {@link #getMode()}. That is because {@link #setMode(Mode)} primarily affects the first of the
   * generated {@link ChainLink}s, while {@link #getMode()} returns the {@link Mode} of the last
   * {@link ChainLink} and is used in conjunction with {@link #canBeDependedOn()}.
   * <p>
   * Subclasses should override this method if they support multiple {@link Mode}s.
   *
   * @param mode
   * @throws IllegalModifierException if this {@link ChainPart} cannot possess the given
   *         {@link Mode}
   */
  default void setMode(Mode mode) throws IllegalModifierException {
    throw new IllegalModifierException(
        "The class " + getClass() + " does not support multiple modes");
  }

  /**
   * Set whether this {@link ChainPart} needs redstone (optional operation).
   * <p>
   * Subclasses should override this method if they support the need for a redstone signal.
   *
   * @param needsRedstone
   * @throws IllegalModifierException if this {@link ChainPart} cannot need a redstone signal
   */
  default void setNeedsRedstone(boolean needsRedstone) throws IllegalModifierException {
    throw new IllegalModifierException(
        "The class " + getClass() + " does not support the need for a redstone signal");
  }
}
