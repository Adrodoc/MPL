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

import java.io.Serializable;
import java.util.Deque;
import java.util.Iterator;

import de.adrodoc55.commons.collections.Queues.UnmodifiableQueue;

public class Deques {
  protected Deques() throws Exception {
    throw new Exception("Utils Classes cannot be instantiated!");
  }

  /**
   * Returns an unmodifiable view of the specified {@link Deque}. This method allows modules to
   * provide users with "read-only" access to internal deques. Query operations on the returned
   * deque "read through" to the specified deque, and attempts to modify the returned deque, whether
   * direct or via its iterator, result in an <tt>UnsupportedOperationException</tt>.
   * <p>
   *
   * The returned deque will be serializable if the specified deque is serializable.
   *
   * @param <T> the class of the objects in the deque
   * @param delegate the deque for which an unmodifiable view is to be returned
   * @return an unmodifiable view of the specified deque
   */
  public static <T> Deque<T> unmodifiableDeque(Deque<? extends T> delegate) {
    return new UnmodifiableDeque<T>(delegate);
  }

  static class UnmodifiableDeque<E> extends UnmodifiableQueue<E>implements Deque<E>, Serializable {
    private static final long serialVersionUID = -6239248934222973995L;
    private final Deque<? extends E> delegate;

    UnmodifiableDeque(Deque<? extends E> delegate) {
      super(delegate);
      this.delegate = delegate;
    }

    @Override
    public void addFirst(E e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void addLast(E e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean offerFirst(E e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean offerLast(E e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public E removeFirst() {
      throw new UnsupportedOperationException();
    }

    @Override
    public E removeLast() {
      throw new UnsupportedOperationException();
    }

    @Override
    public E pollFirst() {
      throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
      throw new UnsupportedOperationException();
    }

    @Override
    public E getFirst() {
      return delegate.getFirst();
    }

    @Override
    public E getLast() {
      return delegate.getLast();
    }

    @Override
    public E peekFirst() {
      return delegate.peekFirst();
    }

    @Override
    public E peekLast() {
      return delegate.peekLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void push(E e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public E pop() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> descendingIterator() {
      return Iterators.unmodifiableIterator(delegate.descendingIterator());
    }
  }
}
