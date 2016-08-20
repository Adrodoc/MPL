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
import java.util.Queue;

import de.adrodoc55.commons.collections.Collections.UnmodifiableCollection;

public class Queues {
  protected Queues() throws Exception {
    throw new Exception("Utils Classes cannot be instantiated!");
  }

  /**
   * Returns an unmodifiable view of the specified {@link Queue}. This method allows modules to
   * provide users with "read-only" access to internal queue. Query operations on the returned queue
   * "read through" to the specified queue, and attempts to modify the returned queue, whether
   * direct or via its iterator, result in an <tt>UnsupportedOperationException</tt>.
   * <p>
   *
   * The returned queue will be serializable if the specified queue is serializable.
   *
   * @param <T> the class of the objects in the queue
   * @param delegate the queue for which an unmodifiable view is to be returned
   * @return an unmodifiable view of the specified queue
   */
  public static <T> Queue<T> unmodifiableQueue(Queue<? extends T> delegate) {
    return new UnmodifiableQueue<T>(delegate);
  }

  static class UnmodifiableQueue<E> extends UnmodifiableCollection<E>
      implements Queue<E>, Serializable {
    private static final long serialVersionUID = -4072656536806434331L;
    private final Queue<? extends E> delegate;

    UnmodifiableQueue(Queue<? extends E> delegate) {
      super(delegate);
      this.delegate = delegate;
    }

    @Override
    public boolean offer(E e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public E remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public E poll() {
      throw new UnsupportedOperationException();
    }

    @Override
    public E element() {
      return delegate.element();
    }

    @Override
    public E peek() {
      return delegate.peek();
    }
  }
}
