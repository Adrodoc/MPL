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
 * This class is able to copy Objects of type {@link Copyable} without creating loops on
 * bidirectional mappings while retaining a correct structure of references.
 *
 * @author Adrodoc55
 */
public class CopyScope {
  private final IdentityHashMap<Object, Object> mapOriginalToCopy = new IdentityHashMap<>();

  /**
   * Returns an independent copy of {@code original}. This returns a deep copy meaning that the
   * {@code original} instance is never affected by any changes made to the copy or it's fields.
   * <br>
   * Every Object is only copied once per {@link CopyScope}, each copy is cached to avoid cycles and
   * retain the structure. This method returns {@code null} if and only if {@code original} is
   * {@code null}.
   *
   * @param <C> the type of {@link Copyable}s
   * @param original the Object to copy
   * @return an independent copy
   */
  public @Nullable <C extends Copyable> C copy(@Nullable C original) {
    if (original == null) {
      return null;
    }
    @SuppressWarnings("unchecked")
    C cached = (C) mapOriginalToCopy.get(original);
    if (cached != null) {
      return cached;
    }
    @SuppressWarnings("unchecked")
    C copy = (C) original.copy(this);
    checkNotNull(copy, "copy == null!");
    mapOriginalToCopy.put(original, copy);
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
   * @return {@code result} the given Collection
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
   * @param original the Object to copy
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
   * @return {@code result} the given Collection
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
   * An interface for the copy constructor pattern. Objects of type {@link Copyable} should be
   * copied by a {@link CopyScope}. To enable inheritance, an implementation of
   * {@link #copy(CopyScope)} should call a protected copy constructor that copies all copyable
   * fields using the scope:
   *
   * <pre>
   * {@code
   *   public class MyCopyable implements Copyable {
   *     private int primitive;
   *     private String notCopyable;
   *
   *     private MyCopyable copyable;
   *     private final List<MyCopyable> finalCopyableCollection = new ArrayList<>();
   *     private Set<MyCopyable> copyableCollection = new HashSet<>();
   *
   *     private Object maybeCopyable;
   *     private final List<String> finalCollection = new ArrayList<>();
   *     private Set<Object> collection = new HashSet<>();
   *
   *     protected MyCopyable(MyCopyable original, CopyScope scope) {
   *       primitive = original.primitive;
   *       notCopyable = original.notCopyable;
   *
   *       copyable = scope.copy(original.copyable);
   *       finalCopyableCollection.addAll(scope.copy(original.finalCopyableCollection));
   *       copyableCollection = scope.copyInto(original.copyableCollection, new HashSet<>());
   *
   *       maybeCopyable = scope.copyObject(original.maybeCopyable);
   *       finalCollection.addAll(scope.copyObjects(original.finalCollection));
   *       collection = scope.copyObjectsInto(original.collection, new HashSet<>());
   *     }
   *
   *     @Deprecated
   *     @Override
   *     public Copyable copy(CopyScope scope) {
   *       return new MyCopyable(this, scope);
   *     }
   *   }
   * }
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
     * Returns an independent copy of {@code this}. This returns a deep copy meaning that
     * {@code this} instance is never affected by any changes made to the copy or it's fields.<br>
     * <b>This method must return an instance of the same runtime class.</b><br>
     * This method must not return {@code null}. If {@code scope} is {@code null} this method may
     * throw a {@link NullPointerException}.
     *
     * @deprecated
     * @param scope the {@link CopyScope}
     * @return an independent copy of {@code this}
     * @throws NullPointerException if {@code scope} is null
     * @see CopyScope#copy(Copyable)
     */
    @Deprecated
    public @Nonnull Copyable copy(CopyScope scope) throws NullPointerException;
  }

}
