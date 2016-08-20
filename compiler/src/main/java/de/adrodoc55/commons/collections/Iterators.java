package de.adrodoc55.commons.collections;

import java.util.Iterator;
import java.util.function.Consumer;

public class Iterators {
  protected Iterators() throws Exception {
    throw new Exception("Utils Classes cannot be instantiated!");
  }

  public static <T> Iterator<T> unmodifiableIterator(Iterator<? extends T> delegate) {
    return new UnmodifiableIterator<T>(delegate);
  }

  static class UnmodifiableIterator<E> implements Iterator<E> {
    private final Iterator<? extends E> delegate;

    UnmodifiableIterator(Iterator<? extends E> delegate) {
      if (delegate == null) {
        throw new NullPointerException();
      }
      this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
      return delegate.hasNext();
    }

    @Override
    public E next() {
      return delegate.next();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
      delegate.forEachRemaining(action);
    }
  }
}
