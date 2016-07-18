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
package de.adrodoc55.minecraft.mpl.ast.chainparts.program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import de.adrodoc55.commons.Named;
import de.adrodoc55.minecraft.mpl.ast.MplNode;
import de.adrodoc55.minecraft.mpl.ast.ProcessType;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.visitor.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode(exclude = "source")
@ToString(exclude = "source")
@Getter
public class MplProcess implements MplNode, Named {
  private final String name;
  private final boolean repeating;
  private final ProcessType type;
  private final MplSource source;
  private final List<ChainPart> chainParts = new ArrayList<>();
  private final List<String> tags = new ArrayList<>();

  public MplProcess() {
    this(null);
  }

  public MplProcess(@Nullable String name) {
    this(name, false, ProcessType.DEFAULT, new ArrayList<>(), null);
  }

  @GenerateMplPojoBuilder
  public MplProcess(@Nullable String name, boolean repeating, ProcessType type,
      Collection<String> tags, @Nullable MplSource source) {
    this.name = name;
    this.repeating = repeating;
    this.type = type;
    setTags(tags);
    this.source = source;
  }

  /**
   * Read only!
   *
   * @return an unmodifiable {@link List} of the {@link ChainPart}s of this {@link MplProcess}
   */
  public List<ChainPart> getChainParts() {
    return Collections.unmodifiableList(chainParts);
  }

  public void setChainParts(Collection<? extends ChainPart> chainParts) {
    this.chainParts.clear();
    addAll(chainParts);
  }

  public void add(ChainPart chainPart) {
    this.chainParts.add(chainPart);
  }

  public void addAll(Collection<? extends ChainPart> chainParts) {
    this.chainParts.addAll(chainParts);
  }

  public void addAll(MplProcess other) {
    this.chainParts.addAll(other.chainParts);
  }

  public List<String> getTags() {
    return Collections.unmodifiableList(tags);
  }

  public void setTags(Collection<String> tags) {
    this.tags.clear();
    this.tags.addAll(tags);
  }

  @Override
  public void accept(MplAstVisitor visitor) {
    visitor.visitProcess(this);
  }

}
