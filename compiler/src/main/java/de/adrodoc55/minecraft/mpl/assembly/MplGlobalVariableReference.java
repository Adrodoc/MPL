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
package de.adrodoc55.minecraft.mpl.assembly;

import static de.adrodoc55.minecraft.mpl.ast.variable.Insertable.checkInsertable;
import static org.apache.commons.io.FilenameUtils.getBaseName;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.adrodoc55.commons.StringUtils;
import de.adrodoc55.minecraft.mpl.ast.variable.MplVariable;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.MplInterpreter;
import de.adrodoc55.minecraft.mpl.interpretation.insert.GlobalVariableInsert;
import de.adrodoc55.minecraft.mpl.interpretation.variable.VariableScope;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Adrodoc55
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MplGlobalVariableReference extends MplReference {
  private @Nullable String fileNameWithoutExtension;
  private @Nonnull String identifier;
  private @Nonnull GlobalVariableInsert insert;
  private @Nonnull MplCompilerContext context;

  public MplGlobalVariableReference(@Nullable String fileNameWithoutExtension, String identifier,
      GlobalVariableInsert insert, Collection<? extends File> imports, MplSource source,
      MplCompilerContext context) throws IllegalArgumentException {
    super(imports, source);
    setFileNameWithoutExtension(fileNameWithoutExtension);
    setIdentifier(identifier);
    setInsert(insert);
    setContext(context);
  }

  public String getQualifiedName() {
    String file = fileNameWithoutExtension != null ? fileNameWithoutExtension + "." : "";
    return file + identifier;
  }

  @Override
  public boolean isContainedIn(MplInterpreter interpreter) {
    boolean fileNameEquals = true;
    if (fileNameWithoutExtension != null) {
      fileNameEquals =
          fileNameWithoutExtension.equals(getBaseName(interpreter.getProgramFile().getName()));
    }
    return fileNameEquals && interpreter.getRootVariableScope().findVariable(identifier) != null;
  }

  @Override
  public void resolve(MplInterpreter interpreter) {
    if (interpreter.getProgram().isScript()) {
      context.addError(new CompilerException(source,
          "The local script variable '" + identifier + "' cannot be inserted"));
      return;
    }
    VariableScope rootScope = interpreter.getRootVariableScope();
    MplVariable<?> variable = rootScope.findVariable(identifier);
    if (variable != null) {
      try {
        insert.setVariable(checkInsertable(variable, source));
      } catch (CompilerException ex) {
        context.addError(ex);
      }
    } else {
      throw new IllegalArgumentException(
          "GlobalVariable is not contained in " + interpreter.getProgramFile());
    }
  }

  @Override
  public CompilerException createAmbigiousException(List<File> files) {
    return new CompilerException(source, getQualifiedName() + " is ambigious. It was found in '"
        + StringUtils.joinWithAnd(files) + "!");
  }

  @Override
  public void handleNotFound() {
    context.addError(
        new CompilerException(source, getQualifiedName() + " cannot be resolved to a variable"));
  }
}
