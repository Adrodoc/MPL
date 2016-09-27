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
package de.adrodoc55.minecraft.mpl.compilation;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.interpretation.MplInclude;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;
import de.adrodoc55.minecraft.mpl.version.MplVersion;

/**
 * @author Adrodoc55
 */
public class MplCompilerContext {
  private final MplVersion version;
  private final CompilerOptions options;
  private final Set<CompilerException> errors = new LinkedHashSet<>();
  private final Set<CompilerException> warnings = new LinkedHashSet<>();
  private final Set<MplInclude> included = new LinkedHashSet<>();
  private final Set<MplInclude> toInclude = new LinkedHashSet<>();

  public MplCompilerContext(CompilerOption... options) {
    this(new CompilerOptions(options));
  }

  public MplCompilerContext(CompilerOptions options) {
    this(MinecraftVersion.getDefault(), options);
  }

  public MplCompilerContext(MplVersion version, CompilerOptions options) {
    this.version = version;
    this.options = options;
  }

  public void addContext(MplCompilerContext context) {
    errors.addAll(context.errors);
    warnings.addAll(context.warnings);
    included.addAll(context.included);
    toInclude.addAll(context.toInclude);
  }

  public MplVersion getVersion() {
    return version;
  }

  public CompilerOptions getOptions() {
    return options;
  }

  /**
   * Adds a {@link CompilerException} wich occured during compilation and should be treated as an
   * error.
   *
   * @param ex the exception that occured
   * @return true if the instance had not yet been posted
   */
  public boolean addError(CompilerException ex) {
    return errors.add(ex);
  }

  public Set<CompilerException> getErrors() {
    return Collections.unmodifiableSet(errors);
  }

  public void clearWarnings() {
    warnings.clear();
  }

  /**
   * Adds a {@link CompilerException} wich occured during compilation and should be treated as a
   * warning.
   *
   * @param ex the exception that occured
   * @return true if the instance had not yet been posted
   */
  public boolean addWarning(CompilerException ex) {
    return warnings.add(ex);
  }

  public Set<CompilerException> getWarnings() {
    return Collections.unmodifiableSet(warnings);
  }

  public void addInclude(MplInclude include) {
    if (!included.contains(include)) {
      toInclude.add(include);
    }
  }

  /**
   * Returns the next include to process. The returned include is removed from the todo-list. If
   * there are no more inclides this method returns {@code null}.
   *
   * @return the next include or null
   */
  public MplInclude getNextInclude() {
    Iterator<MplInclude> iterator = toInclude.iterator();
    if (!iterator.hasNext()) {
      return null;
    }
    MplInclude next = iterator.next();
    toInclude.remove(next);
    included.add(next);
    return next;
  }

}
