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
package de.adrodoc55.minecraft.mpl.ide.fx;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.fx.code.editor.fx.services.internal.DefaultSourceViewerConfiguration;
import org.eclipse.fx.core.AppMemento;

/**
 * @author Adrodoc55
 */
public class MplAppMemento implements AppMemento {
  private final static String KEY_HOVER_WINDOW_WIDTH =
      DefaultSourceViewerConfiguration.class.getName() + ".hoverWindowWidth";
  private final static String KEY_HOVER_WINDOW_HEIGHT =
      DefaultSourceViewerConfiguration.class.getName() + ".hoverWindowHeight";
  private final static String KEY_PROPOSAL_WINDOW_WIDTH =
      DefaultSourceViewerConfiguration.class.getName() + ".proposalWindowWidth";
  private final static String KEY_PROPOSAL_WINDOW_HEIGHT =
      DefaultSourceViewerConfiguration.class.getName() + ".proposalWindowHeight";

  private final Map<String, String> strings = new HashMap<>();
  private final Map<String, Boolean> booleans = new HashMap<>();
  private final Map<String, Integer> integers = new HashMap<>();
  private final Map<String, Double> doubles = new HashMap<>();

  public MplAppMemento() {
    put(KEY_HOVER_WINDOW_WIDTH, 400d);
    put(KEY_HOVER_WINDOW_HEIGHT, 50d);
    put(KEY_PROPOSAL_WINDOW_WIDTH, 400d);
    put(KEY_PROPOSAL_WINDOW_HEIGHT, 50d);
  }

  @Override
  public void put(String key, String value) {
    strings.put(key, value);
  }

  @Override
  public void put(String key, boolean value) {
    booleans.put(key, value);
  }

  @Override
  public void put(String key, int value) {
    integers.put(key, value);
  }

  @Override
  public void put(String key, double value) {
    doubles.put(key, value);
  }

  @Override
  public void put(String key, Object value, String serializer) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void remove(String key) {
    strings.remove(key);
    booleans.remove(key);
    integers.remove(key);
    doubles.remove(key);
  }

  @Override
  public boolean exists(String key) {
    return strings.containsKey(key) || booleans.containsKey(key) || integers.containsKey(key)
        || doubles.containsKey(key);
  }

  @Override
  public String get(String key, String defaultValue) {
    return strings.getOrDefault(key, defaultValue);
  }

  @Override
  public boolean get(String key, boolean defaultValue) {
    return booleans.getOrDefault(key, defaultValue);
  }

  @Override
  public int get(String key, int defaultValue) {
    return integers.getOrDefault(key, defaultValue);
  }

  @Override
  public double get(String key, double defaultValue) {
    return doubles.getOrDefault(key, defaultValue);
  }

  @Override
  public <O> O get(String key, Class<O> clazz, O defaultValue) {
    throw new UnsupportedOperationException();
  }
}
