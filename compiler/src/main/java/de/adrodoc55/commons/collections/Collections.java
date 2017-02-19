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

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Iterables;

/**
 * @author Adrodoc55
 */
public class Collections {
  protected Collections() throws Exception {
    throw new Exception("Utils Classes cannot be instantiated!");
  }

  /**
   * Returns an unmodifiable view of the specified collection. This method allows modules to provide
   * users with "read-only" access to internal collections. Query operations on the returned
   * collection "read through" to the specified collection, and attempts to modify the returned
   * collection, whether direct or via its iterator, result in an
   * <tt>UnsupportedOperationException</tt>.
   * <p>
   *
   * The returned collection does <i>not</i> pass the hashCode and equals operations through to the
   * backing collection, but relies on <tt>Object</tt>'s <tt>equals</tt> and <tt>hashCode</tt>
   * methods. This is necessary to preserve the contracts of these operations in the case that the
   * backing collection is a set or a list.
   * <p>
   *
   * The returned collection will be serializable if the specified collection is serializable.
   *
   * @param <E> the class of the objects in the collection
   * @param c the collection for which an unmodifiable view is to be returned.
   * @return an unmodifiable view of the specified collection.
   */
  public static <E> Collection<E> unmodifiableCollection(Collection<? extends E> c) {
    return new UnmodifiableCollection<>(c);
  }

  static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
    private static final long serialVersionUID = -6104307908883229835L;
    final Collection<? extends E> delegate;

    UnmodifiableCollection(Collection<? extends E> c) {
      if (c == null)
        throw new NullPointerException();
      this.delegate = c;
    }

    @Override
    public int size() {
      return delegate.size();
    }

    @Override
    public boolean isEmpty() {
      return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
      return delegate.contains(o);
    }

    @Override
    public Object[] toArray() {
      return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
      return delegate.toArray(a);
    }

    @Override
    public String toString() {
      return delegate.toString();
    }

    @Override
    public Iterator<E> iterator() {
      return Iterators.unmodifiableIterator(delegate.iterator());
    }

    @Override
    public boolean add(E e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
      return delegate.containsAll(coll);
    }

    @Override
    public boolean addAll(Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
      delegate.forEach(action);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Spliterator<E> spliterator() {
      return (Spliterator<E>) delegate.spliterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> stream() {
      return (Stream<E>) delegate.stream();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> parallelStream() {
      return (Stream<E>) delegate.parallelStream();
    }
  }

  @SafeVarargs
  public static <E> Collection<E> concat(Collection<? extends E>... collecions) {
    return new CombinedCollection<>(Arrays.asList(collecions));
  }

  public static <E> Collection<E> concat(Iterable<? extends Collection<? extends E>> collecions) {
    return new CombinedCollection<>(collecions);
  }

  static class CombinedCollection<E> extends AbstractCollection<E> {
    private final Iterable<? extends Collection<? extends E>> collecions;

    public CombinedCollection(Iterable<? extends Collection<? extends E>> collecions) {
      this.collecions = checkNotNull(collecions, "collecions == null!");
    }

    @Override
    public void clear() {
      for (Collection<? extends E> collection : collecions) {
        collection.clear();
      }
    }

    @Override
    public Iterator<E> iterator() {
      return Iterables.<E>concat(collecions).iterator();
    }

    @Override
    public int size() {
      int result = 0;
      for (Collection<? extends E> collection : collecions) {
        result += collection.size();
      }
      return result;
    }
  }
}
