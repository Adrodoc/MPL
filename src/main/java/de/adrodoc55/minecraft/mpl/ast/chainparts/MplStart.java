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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import de.adrodoc55.minecraft.mpl.ast.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
@Getter
@Setter
public class MplStart extends ModifiableChainPart {
  private final String process;
  private @Nullable Mode mode;

  public MplStart(String process) {
    this(process, new ModifierBuffer());
  }

  public MplStart(String process, ModifierBuffer modifier) {
    super(modifier);
    this.process = checkNotNull(process, "process == null!");
  }

  @GenerateMplPojoBuilder
  public MplStart(String process, ModifierBuffer modifier, @Nullable Dependable previous) {
    super(modifier, previous);
    this.process = checkNotNull(process, "process == null!");
  }

  @Override
  public String getName() {
    return "start";
  }

  @Override
  public void accept(MplAstVisitor visitor) {
    visitor.visitStart(this);
  }

  @Override
  public boolean canBeDependedOn() {
    return true;
  }

  @Override
  public Mode getModeForInverting() {
    return getMode();
  }
}
