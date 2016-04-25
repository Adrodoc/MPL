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
package de.adrodoc55.minecraft.mpl.chain;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Skip;
import de.adrodoc55.minecraft.mpl.commands.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
public class MplProcess implements ChainPartContainer {

  private final String name;
  private final boolean repeating;
  private final MplSource source;
  private final List<ChainPart> chainParts = new ArrayList<>();

  public MplProcess(@Nonnull String name) {
    this(name, false, null);
  }

  public MplProcess(@Nonnull String name, @Nullable MplSource source) {
    this(name, false, source);
  }

  @GenerateMplPojoBuilder
  public MplProcess(@Nonnull String name, boolean repeating, @Nullable MplSource source) {
    this.name = checkNotNull(name, "name == null!");
    this.repeating = repeating;
    this.source = source;
  }

  public @Nonnull String getName() {
    return name;
  }

  public boolean isRepeating() {
    return repeating;
  }

  public @Nullable MplSource getSource() {
    return source;
  }

  @Override
  public @Nonnull List<ChainPart> getChainParts() {
    return Collections.unmodifiableList(chainParts);
  }

  public void setChainParts(@Nonnull Collection<ChainPart> chainParts) {
    this.chainParts.clear();
    this.chainParts.addAll(chainParts);
  }

  @Override
  public NamedCommandChain toCommandChain(CompilerOptions options) {
    List<ChainLink> chainLinks = new ArrayList<>();
    if (options.hasOption(TRANSMITTER)) {
      chainLinks.add(new Skip(false));
    }
    if (isRepeating()) {
      // if (chainParts.isEmpty()) {
      chainLinks.add(new InternalCommand("", REPEAT, false));
      // } else {
      // ChainPart first = chainParts.get(0);
      // first.setMode(REPEAT);
      // }
    } else {
      if (options.hasOption(TRANSMITTER)) {
        chainLinks.add(new InternalCommand("/setblock ${this - 1} stone", Mode.IMPULSE, false));
      } else {
        chainLinks.add(new InternalCommand("/entitydata ~ ~ ~ {auto:0}", Mode.IMPULSE, false));
      }
    }
    chainLinks.addAll(toChainLinks(options));
    return new NamedCommandChain(getName(), chainLinks);
  }

  @Override
  public String toString() {
    return getName();
  }
}
