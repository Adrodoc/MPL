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
package de.adrodoc55.minecraft.mpl.commands.chainlinks;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static java.util.Arrays.asList;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.commands.Dependable;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.Modifiable;
import de.adrodoc55.minecraft.mpl.interpretation.CommandPartBuffer;
import de.adrodoc55.minecraft.mpl.interpretation.UnableToResolveInsertException;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;
import de.adrodoc55.minecraft.mpl.interpretation.insert.TargetedThisInsert;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode
@Getter
@Setter
public class Command implements ChainLink, Modifiable {
  private @Nonnull CommandPartBuffer minecraftCommand;
  private @Nonnull Mode mode;
  private boolean conditional;
  private boolean needsRedstone;
  private GeneratedBy generatedBy;

  @GenerateMplPojoBuilder
  @VisibleForTesting
  Command(CommandPartBuffer minecraftCommand, Mode mode, boolean conditional, boolean needsRedstone,
      GeneratedBy generatedBy) {
    this.minecraftCommand = checkNotNull(minecraftCommand, "minecraftCommand == null!");
    setModifier(mode, conditional, needsRedstone);
    setGeneratedBy(generatedBy);
  }

  Command(CommandPartBuffer minecraftCommand, Modifiable modifier, GeneratedBy generatedBy) {
    this.minecraftCommand = checkNotNull(minecraftCommand, "minecraftCommand == null!");
    setModifier(modifier);
    setGeneratedBy(generatedBy);
  }

  @Deprecated
  protected Command(Command original, CopyScope scope) {
    mode = original.mode;
    conditional = original.conditional;
    needsRedstone = original.needsRedstone;
  }

  @Deprecated
  @Override
  public Command createFlatCopy(CopyScope scope) {
    return new Command(this, scope);
  }

  @Deprecated
  @Override
  public void completeDeepCopy(CopyScope scope) {
    Command original = scope.getCache().getOriginal(this);
    minecraftCommand = scope.copyObject(original.minecraftCommand);
  }

  public void setModifier(Modifiable modifier) {
    setModifier(modifier.getMode(), modifier.isConditional(), modifier.getNeedsRedstone());
  }

  public void setModifier(Mode mode, boolean conditional, boolean needsRedstone) {
    this.mode = Mode.nonNull(mode);
    this.conditional = conditional;
    this.needsRedstone = needsRedstone;
  }

  @Override
  public boolean getNeedsRedstone() {
    return needsRedstone;
  }

  public List<Object> getCommandParts() {
    return minecraftCommand.getCommandParts();
  }

  public String getCommand() {
    return Joiner.on("").join(getCommandParts());
  }

  @Override
  public MplBlock toBlock(Coordinate3D coordinate, Direction3 direction) {
    return new CommandBlock(this, direction, coordinate);
  }

  @Override
  public void resolveTargetedThisInserts(Iterable<? extends ChainLink> chainLinks) {
    minecraftCommand.resolveTargetedThisInserts(insert -> resolve(insert, chainLinks));
  }

  private RelativeThisInsert resolve(TargetedThisInsert insert,
      Iterable<? extends ChainLink> chainLinks) throws UnableToResolveInsertException {
    int self = Iterables.indexOf(chainLinks, it -> it == this);
    if (self == -1) {
      throwNotFoundException("This");
    }
    Dependable target = insert.getTarget();
    int ref = Iterables.indexOf(chainLinks, it -> it == target);
    if (ref == -1) {
      throwNotFoundException("The referenced chainLink");
    }
    return new RelativeThisInsert(ref - self);
  }

  private void throwNotFoundException(String string) throws UnableToResolveInsertException {
    throw new UnableToResolveInsertException(
        "Failed to resolve reference. " + string + " was not found in the specified chainLinks");
  }

  @Override
  public String toString() {
    String mode = this.mode != Mode.CHAIN ? this.mode.toString() : null;
    String conditional = this.conditional ? "conditional" : null;
    String needsRedstone = this.needsRedstone ? "needs redstone" : null;
    String modifiers = on("; ").join(filter(asList(mode, conditional, needsRedstone), notNull()));
    return modifiers + (modifiers.isEmpty() ? "" : ": ") + "/" + getCommand();
  }
}
