package de.adrodoc55.commons.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
   * @param <T> the class of the objects in the collection
   * @param c the collection for which an unmodifiable view is to be returned.
   * @return an unmodifiable view of the specified collection.
   */
  public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> c) {
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
}
