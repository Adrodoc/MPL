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
package de.adrodoc55.commons;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is able to copy objects of type {@link Copyable} without throwing a
 * {@link StackOverflowError} on bidirectional mappings while retaining a correct structure of
 * references.
 *
 * @author Adrodoc55
 */
public class CopyScope {

  /**
   * The underlying {@link CopyCache} that keeps track of all objects copied by {@code this}
   * {@link CopyScope}.
   */
  private final CopyCache cache = new CopyCache();

  /**
   * Returns the underlying {@link CopyCache} that keeps track of all objects copied by {@code this}
   * {@link CopyScope}.
   *
   * @return the underlying {@link CopyCache}
   */
  public @Nonnull CopyCache getCache() {
    return cache;
  }

  /**
   * Returns an independent copy of {@code original}. This returns a deep copy meaning that the
   * {@code original} instance is never affected by any changes made to the copy or it's fields.
   * <br>
   * Every object is only copied once per {@link CopyScope}, each copy is cached to avoid cycles and
   * retain the structure. The cache can be accessed with {@link #getCache()}. This method returns
   * {@code null} if and only if {@code original} is {@code null}.
   *
   * @param <C> the type of {@link Copyable}
   * @param original the object to copy
   * @return an independent copy
   */
  public @Nullable <C extends Copyable> C copy(@Nullable C original) {
    if (original == null) {
      return null;
    }
    C cached = cache.getCopyOrNull(original);
    if (cached != null) {
      return cached;
    }
    @SuppressWarnings("unchecked")
    C copy = (C) original.createFlatCopy(this);
    checkNotNull(copy, "copy == null!");
    cache.put(original, copy);
    copy.completeDeepCopy(this);
    return copy;
  }

  /**
   * Copy all elements in {@code originals}. This is a shortcut for
   * {@link #copyInto(Iterable, Collection)}
   *
   * @param <C> the type of {@link Copyable}s
   * @param originals the elements to copy
   * @return a list containing all copies
   * @throws NullPointerException if any parameter is {@code null}
   * @see #copyInto(Iterable, Collection)
   */
  public @Nonnull <C extends Copyable> List<C> copy(@Nonnull Collection<? extends C> originals)
      throws NullPointerException {
    checkNotNull(originals, "originals == null!");
    return copyInto(originals, new ArrayList<>(originals.size()));
  }

  /**
   * Copy all elements in {@code originals} into {@code result}. This method returns the parameter
   * {@code result} to allow easy assignments:<br>
   *
   * <pre>
   * {@code Set<MyCopyable> copies = scope.copyInto(originals, new HashSet<>())}
   * </pre>
   *
   * @param <C> the type of {@link Copyable}s
   * @param <R> the type of Collection to fill
   * @param originals the elements to copy
   * @param result the Collection to fill
   * @return {@code result} the specified Collection
   * @throws NullPointerException if any parameter is {@code null}
   */
  public @Nonnull <C extends Copyable, R extends Collection<C>> R copyInto(
      @Nonnull Iterable<? extends C> originals, @Nonnull R result) throws NullPointerException {
    checkNotNull(originals, "originals == null!");
    checkNotNull(result, "result == null!");
    for (C original : originals) {
      C copy = (C) copy(original);
      result.add(copy);
    }
    return result;
  }

  /**
   * Returns an independent copy of {@code original} if it is {@link Copyable}, otherwise return
   * {@code original}. This method returns {@code null} if and only if {@code original} is
   * {@code null}.
   *
   * @param <O> the type to copy
   * @param original the object to copy
   * @return an independent copy or {@code original} if it is not {@link Copyable}
   * @see #copy(Copyable)
   */
  public @Nullable <O> O copyObject(@Nullable O original) {
    if (original instanceof Copyable) {
      @SuppressWarnings("unchecked")
      O copy = (O) copy((Copyable) original);
      return copy;
    } else {
      return original;
    }
  }

  /**
   * Copy all elements in {@code originals}. This is a shortcut for
   * {@link #copyObjectsInto(Iterable, Collection)}
   *
   * @param <O> the type to copy
   * @param originals the elements to copy
   * @return a list containing all copies
   * @throws NullPointerException if {@code originals} is {@code null}
   * @see #copyObjectsInto(Iterable, Collection)
   */
  public @Nonnull <O> List<O> copyObjects(@Nonnull Collection<? extends O> originals)
      throws NullPointerException {
    checkNotNull(originals, "originals == null!");
    return copyObjectsInto(originals, new ArrayList<>(originals.size()));
  }

  /**
   * Copy all elements in {@code originals} into {@code result} using {@link #copyObject(Object)}.
   * This method returns the parameter {@code result} to allow easy assignments:<br>
   *
   * <pre>
   * {@code Set<Object> copies = scope.copyInto(originals, new HashSet<>())}
   * </pre>
   *
   * @param <O> the type to copy
   * @param <R> the type of Collection to fill
   * @param originals the elements to copy
   * @param result the Collection to fill
   * @return {@code result} the specified Collection
   * @throws NullPointerException if any parameter is {@code null}
   * @see #copyObject(Object)
   */
  public @Nonnull <O, R extends Collection<O>> R copyObjectsInto(
      @Nonnull Iterable<? extends O> originals, @Nonnull R result) throws NullPointerException {
    checkNotNull(originals, "originals == null!");
    checkNotNull(result, "result == null!");
    for (O original : originals) {
      O copy = copyObject(original);
      result.add(copy);
    }
    return result;
  }

  /**
   * This class caches the copies and originals of a {@link CopyScope} and provides read only access
   * to the chached objects.
   *
   * @author Adrodoc55
   */
  public static class CopyCache {
    private final IdentityHashMap<Object, Object> mapOriginalToCopy = new IdentityHashMap<>();
    private final IdentityHashMap<Object, Object> mapCopyToOriginal = new IdentityHashMap<>();

    /**
     * Caches and associates the specified {@code original} with the specified {@code copy}.
     *
     * @param original the original {@link Copyable} that was copied
     * @param copy the copy created by a {@link CopyScope}
     * @throws NullPointerException if any parameter is {@code null}
     */
    private <C extends Copyable> void put(@Nonnull C original, @Nonnull C copy)
        throws NullPointerException {
      checkNotNull(original, "original == original!");
      checkNotNull(copy, "copy == null!");
      mapOriginalToCopy.put(original, copy);
      mapCopyToOriginal.put(copy, original);
    }

    /**
     * Returns the number of cached original-copy associations.
     *
     * @return the size of this cache
     */
    public int size() {
      int sizeOtC = mapOriginalToCopy.size();
      int sizeCtO = mapCopyToOriginal.size();
      if (sizeOtC != sizeCtO) {
        String message = String.format("Unexpected size difference in CopyCache: OtC:%d, CtO:%d",
            sizeOtC, sizeCtO);
        throw new IllegalStateException(message);
      }
      return sizeOtC;
    }

    /**
     * Returns the cached copy of the specified {@code original}. If {@code original} is
     * {@code null} or was not copied using {@code this} cache, this method returns {@code null}.
     *
     * @param <C> the type of {@link Copyable}
     * @param original the original object
     * @return the cached copy or {@code null}
     */
    public @Nullable <C extends Copyable> C getCopyOrNull(@Nullable C original) {
      @SuppressWarnings("unchecked")
      C copy = (C) mapOriginalToCopy.get(original);
      return copy;
    }

    /**
     * Returns the cached copy of the specified {@code original}. If {@code original} was not copied
     * using {@code this} cache, this method will throw an {@link IllegalArgumentException}. This
     * method returns {@code null} if and only if {@code original} is {@code null}.
     *
     * @param <C> the type of {@link Copyable}
     * @param original the original object
     * @return the cached copy
     * @throws IllegalArgumentException if {@code original} was not copied using this cache
     */
    public @Nullable <C extends Copyable> C getCopy(@Nullable C original)
        throws IllegalArgumentException {
      if (original == null) {
        return null;
      }
      C copy = getCopyOrNull(original);
      if (copy != null)
        return copy;
      throw new IllegalArgumentException(
          "The specified instance was not copied using this CopyScope!");
    }

    /**
     * Returns the cached original of the specified {@code copy}. If {@code copy} is {@code null} or
     * was not created using {@code this} cache, this method returns {@code null}.
     *
     * @param <C> the type of {@link Copyable}
     * @param copy the copied object
     * @return the cached original or {@code null}
     */
    public @Nullable <C extends Copyable> C getOriginalOrNull(@Nullable C copy) {
      @SuppressWarnings("unchecked")
      C original = (C) mapCopyToOriginal.get(copy);
      return original;
    }

    /**
     * Returns the cached original of the specified {@code copy}. If {@code copy} was not created
     * using {@code this} cache, this method will throw an {@link IllegalArgumentException}. This
     * method returns {@code null} if and only if {@code copy} is {@code null}.
     *
     * @param <C> the type of {@link Copyable}
     * @param copy the copied object
     * @return the cached original
     * @throws IllegalArgumentException if {@code copy} was not created using this cache
     */
    public @Nullable <C extends Copyable> C getOriginal(@Nullable C copy)
        throws IllegalArgumentException {
      if (copy == null) {
        return null;
      }
      C original = getOriginalOrNull(copy);
      if (original != null)
        return original;
      throw new IllegalArgumentException(
          "The specified instance was not created using this CopyCache!");
    }
  }

  /**
   * An interface for the copy constructor pattern. Objects of type {@link Copyable} should be
   * copied by a {@link CopyScope}. To enable inheritance, an implementation of
   * {@link #createFlatCopy(CopyScope)} should call a protected copy constructor that copies all
   * final fields and fields that are not {@link Copyable} themself. Non-final references to other
   * {@link Copyable} objects should be filled in {@link #completeDeepCopy(CopyScope)} by using the
   * provided {@link CopyScope}.
   *
   * <pre>
   * <code>
   * public class MyCopyable implements Copyable {
   *   private int primitive;
   *   private String notCopyable;
   *   private final MyCopyable finalCopyable;
   *
   *   private MyCopyable nonFinalCopyable;
   *   private final List&lt;MyCopyable&gt; finalCopyableCollection = new ArrayList&lt;&gt;();
   *   private Set&lt;MyCopyable&gt; copyableCollection = new HashSet&lt;&gt;();
   *
   *   private Object maybeCopyable;
   *   private final List&lt;String&gt; finalCollection = new ArrayList&lt;&gt;();
   *   private Set&lt;Object&gt; collection = new HashSet&lt;&gt;();
   *
   *   &#64;Deprecated
   *   protected MyCopyable(MyCopyable original, CopyScope scope) {
   *     primitive = original.primitive;
   *     notCopyable = original.notCopyable;
   *     finalCopyable = scope.copy(original.finalCopyable);
   *   }
   *
   *   &#64;Deprecated
   *   &#64;Override
   *   public Copyable createFlatCopy(CopyScope scope) throws NullPointerException {
   *     return new MyCopyable(this, scope);
   *   }
   *
   *   &#64;Deprecated
   *   &#64;Override
   *   public void completeDeepCopy(CopyScope scope) throws NullPointerException {
   *     MyCopyable original = scope.getCache().getOriginal(this);
   *
   *     nonFinalCopyable = scope.copy(original.nonFinalCopyable);
   *     finalCopyableCollection.addAll(scope.copy(original.finalCopyableCollection));
   *     copyableCollection = scope.copyInto(original.copyableCollection, new HashSet&lt;&gt;());
   *
   *     maybeCopyable = scope.copyObject(original.maybeCopyable);
   *     finalCollection.addAll(scope.copyObjects(original.finalCollection));
   *     collection = scope.copyObjectsInto(original.collection, new HashSet&lt;&gt;());
   *   }
   * }
   * </code>
   * </pre>
   *
   * @author Adrodoc55
   */
  public interface Copyable {
    /**
     * <b>This method should not be called, instead use {@link CopyScope#copy(Copyable)}. All
     * subclasses should also annotate their implementation of this method with {@link Deprecated
     * &#64;Deprecated}.</b><br>
     *
     * Returns an independent flat copy, meaning that {@code this} instance is never affected by any
     * changes made to the copy or it's fields. References to other {@link Copyable}s should not be
     * filled by this method unless they are final. Those non-final references should be filled in
     * the implementation of {@link #completeDeepCopy(CopyScope)} by using the provided
     * {@link CopyScope}. <b>This method must return an instance of the same runtime class.</b><br>
     * This method must not return {@code null}. If {@code scope} is {@code null} this method may
     * throw a {@link NullPointerException}.
     *
     * @deprecated
     * @param scope the {@link CopyScope}
     * @return an independent flat copy of {@code this}
     * @throws NullPointerException if {@code scope} is null
     * @see CopyScope#copy(Copyable)
     */
    @Deprecated
    public @Nonnull Copyable createFlatCopy(CopyScope scope) throws NullPointerException;

    /**
     * <b>This method should not be called, instead use {@link CopyScope#copy(Copyable)}. All
     * subclasses should also annotate their implementation of this method with {@link Deprecated
     * &#64;Deprecated}.</b><br>
     *
     * Modifies this instance to fill all non-final references to other {@link Copyable}s by using
     * the provided {@link CopyScope}. This two step process avoids a {@link StackOverflowError}.
     * The original object can be accessed through the {@link CopyCache}:
     *
     * <pre>
     * {@code MyCopyable original = scope.getCache().getOriginal(this)}
     * </pre>
     *
     * If {@code scope} is {@code null} this method may throw a {@link NullPointerException}.
     *
     * @deprecated
     * @param scope the {@link CopyScope}
     * @throws NullPointerException if {@code scope} is null
     * @see CopyScope#copy(Copyable)
     */
    @Deprecated
    public default void completeDeepCopy(CopyScope scope) throws NullPointerException {}
  }
}
