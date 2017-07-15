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
package de.adrodoc55.commons.collections;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterables;

/**
 * @author Adrodoc55
 */
public class Lists {
  protected Lists() throws Exception {
    throw new Exception("Utils Classes cannot be instantiated!");
  }

  @SafeVarargs
  public static <E> List<E> concat(List<? extends E>... lists) {
    return new CombinedList<>(Arrays.asList(lists));
  }

  public static <E> Collection<E> concat(Iterable<? extends List<? extends E>> lists) {
    return new CombinedList<>(lists);
  }

  static class CombinedList<E> extends AbstractList<E> {
    private final Iterable<? extends List<? extends E>> lists;

    public CombinedList(Iterable<? extends List<? extends E>> lists) {
      this.lists = checkNotNull(lists, "lists == null!");
    }

    @Override
    public E get(int index) {
      for (List<? extends E> list : lists) {
        int size = list.size();
        if (index < size) {
          return list.get(index);
        } else {
          index -= size;
        }
      }
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
    }

    @Override
    public void clear() {
      for (List<? extends E> list : lists) {
        list.clear();
      }
    }

    @Override
    public Iterator<E> iterator() {
      return Iterables.<E>concat(lists).iterator();
    }

    @Override
    public int size() {
      int result = 0;
      for (List<? extends E> list : lists) {
        result += list.size();
      }
      return result;
    }
  }
}
