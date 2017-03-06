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
package de.adrodoc55.minecraft.mpl.ast.variable.selector;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.minecraft.mpl.antlr.TargetSelectorBaseListener;
import de.adrodoc55.minecraft.mpl.antlr.TargetSelectorParser.ArgumentContext;
import de.adrodoc55.minecraft.mpl.antlr.TargetSelectorParser.SelectorContext;
import de.adrodoc55.minecraft.mpl.antlr.TargetSelectorParser.ValueContext;

/**
 * @author Adrodoc55
 */
public class TargetSelectorListenerImpl extends TargetSelectorBaseListener {
  private final Map<String, String> arguments = new LinkedHashMap<>();
  private final TargetSelectorBuilder builder =
      new TargetSelectorBuilder().withArguments(arguments);

  @Override
  public void enterSelector(SelectorContext ctx) {
    TerminalNode selectorType = ctx.TARGET_SELECTOR_TYPE();
    String typeString = selectorType.getText().toUpperCase(Locale.ENGLISH);
    TargetSelectorType type = TargetSelectorType.valueOf(typeString);
    builder.withType(type);
  }

  @Override
  public void enterArgument(ArgumentContext ctx) {
    String key = ctx.key().getText();
    ValueContext valueCtx = ctx.value();
    String value = valueCtx != null ? valueCtx.getText() : "";
    if (ctx.EXCLAMATION_MARK() != null) {
      value = '!' + value;
    }
    arguments.put(key, value);
  }

  public TargetSelector getResult() {
    return builder.build();
  }
}
