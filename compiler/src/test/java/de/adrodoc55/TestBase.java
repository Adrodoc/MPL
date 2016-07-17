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
package de.adrodoc55;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import net.karneim.pojobuilder.Builder;

public class TestBase {

  private static final Random RANDOM = new Random(5);

  public static int someInt() {
    return RANDOM.nextInt();
  }

  public static int someInt(int bound) {
    return RANDOM.nextInt(bound);
  }

  /**
   * @param lowerBound inclusive
   * @param upperBound inclusive
   * @return
   */
  public static int someInt(int lowerBound, int upperBound) {
    return RANDOM.nextInt(upperBound + 1 - lowerBound) + lowerBound;
  }

  public static int somePositiveInt() {
    return RANDOM.nextInt(Integer.MAX_VALUE);
  }

  public static int few() {
    return someInt(2, 4);
  }

  public static int several() {
    return someInt(5, 10);
  }

  public static int many() {
    return someInt(11, 100);
  }

  public static Builder<String> $String() {
    return new Builder<String>() {
      @Override
      public String build() {
        return "String" + someInt();
      }
    };
  }

  public static Builder<Boolean> $boolean() {
    return new Builder<Boolean>() {
      @Override
      public Boolean build() {
        return RANDOM.nextBoolean();
      }
    };
  }

  public static <P> P some(Builder<P> builder) {
    return builder.build();
  }

  @SafeVarargs
  public static <C> OneOf<C> $oneOf(C... choices) {
    return $oneOf(Arrays.asList(choices));
  }

  public static <C> OneOf<C> $oneOf(Iterable<C> choices) {
    return new OneOf<>(choices);
  }

  public static class OneOf<C> implements Builder<C> {
    private final Map<C, Integer> counter;
    private final Iterator<C> it;

    public OneOf(Iterable<C> choices) {
      int size = Iterables.size(choices);
      counter = new IdentityHashMap<>(size);
      it = Iterators.cycle(choices);
      Iterators.advance(it, someInt(size));
    }

    public int getCount(C choice) {
      Integer integer = counter.get(choice);
      if (integer == null)
        return 0;
      else
        return integer;
    }

    @Override
    public C build() {
      C next = it.next();
      counter.put(next, getCount(next) + 1);
      return next;
    }

  }

  /**
   * TODO: This is a workaround for pojobuilder #100
   *
   * @return
   */
  @Deprecated
  public static Builder<Collection<ChainLink>> $chainLinkCollectionOf(int count,
      Builder<? extends ChainLink> prototype) {
    return new Builder<Collection<ChainLink>>() {
      @SuppressWarnings("unchecked")
      @Override
      public List<ChainLink> build() {
        return (List<ChainLink>) listOf(count, prototype);
      }
    };
  }

  public static <P> Builder<List<P>> $listOf(int count, Builder<P> prototype) {
    return new Builder<List<P>>() {
      @Override
      public List<P> build() {
        return listOf(count, prototype);
      }
    };
  }

  @SafeVarargs
  public static <E> List<E> listOf(E... elements) {
    return Arrays.asList(elements);
  }

  public static <P> List<P> listOf(int count, Builder<P> prototype) {
    List<P> list = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      list.add(prototype.build());
    }
    return list;
  }

}
