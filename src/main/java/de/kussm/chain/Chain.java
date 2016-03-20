/*
 * Copyright 2015-2016 by the authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.kussm.chain;

import static de.kussm.chain.ChainLink.CONDITIONAL;
import static de.kussm.chain.ChainLink.RECEIVER;
import static de.kussm.chain.ChainLink.TRANSMITTER;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

/**
 * A chain of {@link ChainLinks}.</pos> A valid chain must obey the following rules:
 * <ul>
 * <li>a valid chain must not be empty
 * <li>a {@link ChainLink#TRANSMITTER} is not followed by a {@link ChainLink#RECEIVER}
 * <li>A {@link ChainLink#RECEIVER} is not preceded by a {@link ChainLink#TRANSMITTER}
 * <li>The first or the last {@link chainLink} must not be a {@link ChainLink#CONDITIONAL}
 * </ul>
 * <p>
 * Tests can be found in {@link ChainSpec}.
 *
 * @author Michael Kuß
 */
@lombok.EqualsAndHashCode
public class Chain implements Iterable<ChainLink> {
  private ChainLink[] chainLinks;

  /**
   * Private constructor - use {@link #of(ChainLink...)} to create a {@link Chain}
   * <p>
   *
   * @param chainLinks
   * @throws NullPointerException if
   *         <ul>
   *         <li>{@code chainLinks} is null
   *         <li>one of the elements of {@code chainLinks} is null
   *         </ul>
   * @throws IllegalArgumentException if
   *         <ul>
   *         <li>the array {@code chainLinks} is empty
   *         <li>a {@link ChainLink#TRANSMITTER TRANSMITTER_RECEIVER} is not followed by a
   *         {@link ChainLink#RECEIVER RECEIVER}
   *         <li>A {@link ChainLink#RECEIVER RECEIVER} is followed by a {@link ChainLink#TRANSMITTER
   *         TRANSMITTER_RECEIVER}
   *         <li>A {@link ChainLink#RECEIVER RECEIVER} is not preceded by a
   *         {@link ChainLink#TRANSMITTER TRANSMITTER_RECEIVER}
   *         <li>The first or the last chain link is a {@link ChainLink#CONDITIONAL CONDITIONAL}
   *         </ul>
   */
  private Chain(ChainLink... chainLinks) {
    Preconditions.checkNotNull(chainLinks, "chainLinks must not be null");
    Preconditions.checkArgument(chainLinks.length > 0, "chain must not be empty");
    Preconditions.checkArgument(chainLinks[0] != CONDITIONAL, "chain must not start with a %s",
        CONDITIONAL);
    // Preconditions.checkArgument(chainLinks[chainLinks.length - 1] != CONDITIONAL,
    // "chain must not end with a %s", CONDITIONAL);
    for (int i = 0; i < chainLinks.length; i++) {
      Preconditions.checkNotNull(chainLinks[i], "chain link at index %s must not be null", i);
      if (chainLinks[i] == TRANSMITTER) {
        Preconditions.checkArgument(i + 1 < chainLinks.length && chainLinks[i + 1] == RECEIVER,
            "%s at index %s is not followed by a %s", TRANSMITTER, i, RECEIVER);
      }
      if (chainLinks[i] == RECEIVER) {
        Preconditions.checkArgument(i - 1 >= 0 && chainLinks[i - 1] == TRANSMITTER,
            "%s at index %s is not preceded by a %s", RECEIVER, i, TRANSMITTER);
        Preconditions.checkArgument(i + 1 >= chainLinks.length || chainLinks[i + 1] != TRANSMITTER,
            "%s at index %s is followed by a %s", RECEIVER, i, TRANSMITTER);
      }
    }
    this.chainLinks = new ChainLink[chainLinks.length];
    System.arraycopy(chainLinks, 0, this.chainLinks, 0, chainLinks.length);
  }

  /**
   * Public factory method
   *
   * @param chainLinks
   * @throws NullPointerException if
   *         <ul>
   *         <li>{@code chainLinks} is null
   *         <li>one of the elements of {@code chainLinks} is null
   *         </ul>
   * @throws IllegalArgumentException if
   *         <ul>
   *         <li>the array {@code chainLinks} is empty
   *         <li>a {@link ChainLink#TRANSMITTER TRANSMITTER_RECEIVER} is not followed by a
   *         {@link ChainLink#RECEIVER RECEIVER}
   *         <li>A {@link ChainLink#RECEIVER RECEIVER} is followed by a {@link ChainLink#TRANSMITTER
   *         TRANSMITTER_RECEIVER}
   *         <li>A {@link ChainLink#RECEIVER RECEIVER} is not preceded by a
   *         {@link ChainLink#TRANSMITTER TRANSMITTER_RECEIVER}
   *         <li>The first or the last chain link is a {@link ChainLink#CONDITIONAL CONDITIONAL}
   *         </ul>
   */
  public static Chain of(ChainLink... chainLinks) {
    return new Chain(chainLinks);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + Arrays.asList(chainLinks).stream()
        .map((ChainLink c) -> c.name().substring(0, 1)).collect(Collectors.joining()) + ")";
  }

  /**
   * Returns the number of chain links of this chain.
   *
   * @return
   */
  public int size() {
    return chainLinks.length;
  }


  /**
   * Gets the first chain link of this chain.
   * <p>
   * Same as {@code get(0)}
   *
   * @return
   */
  public ChainLink getFirstChainLink() {
    return chainLinks[0];
  }

  /**
   * Gets the chain link at the given index.
   *
   * @return
   * @throws IndexOutOfBoundsException if {@code index} is out of bounds.
   */
  public ChainLink get(int index) {
    Preconditions.checkElementIndex(index, chainLinks.length, "chain link index");
    return chainLinks[index];
  }

  @Override
  public ListIterator<ChainLink> iterator() {
    return Arrays.asList(chainLinks).listIterator();
  }
}
