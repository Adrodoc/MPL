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
package de.adrodoc55.minecraft.mpl.ast.variable.type;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.adrodoc55.minecraft.mpl.ast.variable.MplIntegerVariable;
import de.adrodoc55.minecraft.mpl.ast.variable.MplStringVariable;
import de.adrodoc55.minecraft.mpl.ast.variable.MplVariable;
import de.adrodoc55.minecraft.mpl.ast.variable.selector.TargetSelector;
import de.adrodoc55.minecraft.mpl.ast.variable.value.MplValue;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;

/**
 * @author Adrodoc55
 */
@Immutable
public abstract class MplType<T> {
  private static ImmutableList<MplType<?>> VALUES;

  public static ImmutableList<MplType<?>> values() {
    if (VALUES == null) {
      VALUES =
          ImmutableList.copyOf(Lists.transform(Arrays.asList(Type.values()), t -> t.getType()));
    }
    return VALUES;
  }

  public static MplType<?> valueOf(String name) {
    return Type.valueOf(name).getType();
  }

  public static final MplType<Integer> INTEGER = new MplType<Integer>(Type.INTEGER) {
    @Override
    public Integer convert(String value, MplSource source, MplCompilerContext context) {
      return Integer.parseInt(value);
    }

    @Override
    public MplVariable<Integer> newVariable(MplSource declarationSource, String identifier) {
      return new MplIntegerVariable(declarationSource, identifier);
    }
  };
  public static final MplType<TargetSelector> SELECTOR =
      new MplType<TargetSelector>(Type.SELECTOR) {
        @Override
        public TargetSelector convert(String value, MplSource source, MplCompilerContext context) {
          return TargetSelector.parse(value, source, context);
        }

        @Override
        public MplVariable<TargetSelector> newVariable(MplSource declarationSource,
            String identifier) {
          return new MplVariable<>(declarationSource, SELECTOR, identifier);
        }
      };
  public static final MplType<String> STRING = new MplType<String>(Type.STRING) {
    @Override
    public String convert(String value, MplSource source, MplCompilerContext context) {
      return checkNotNull(value, "value == null!");
    }

    @Override
    public MplVariable<String> newVariable(MplSource declarationSource, String identifier) {
      return new MplStringVariable(declarationSource, identifier);
    }
  };
  public static final MplType<MplValue> VALUE = new MplType<MplValue>(Type.VALUE) {
    @Override
    public MplValue convert(String value, MplSource source, MplCompilerContext context) {
      return MplValue.parse(value, source, context);
    }

    @Override
    public MplVariable<MplValue> newVariable(MplSource declarationSource, String identifier) {
      return new MplVariable<>(declarationSource, VALUE, identifier);
    }
  };
  private final Type type;

  public MplType(Type type) {
    this.type = checkNotNull(type, "type == null!");
  }

  public boolean isAssignableFrom(MplType<?> type) {
    return this == checkNotNull(type, "type == null!");
  }

  public abstract T convert(String value, MplSource source, MplCompilerContext context);

  public abstract MplVariable<T> newVariable(MplSource declarationSource, String identifier);

  @Override
  public String toString() {
    return type.toString();
  }
}
