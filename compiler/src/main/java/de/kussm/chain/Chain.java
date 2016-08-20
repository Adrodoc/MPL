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
package de.kussm.chain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.kussm.chain.ChainLinkType.CONDITIONAL;
import static de.kussm.chain.ChainLinkType.RECEIVER;
import static de.kussm.chain.ChainLinkType.TRANSMITTER;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;

/*
 * <li>a {@link ChainLinkType#TRANSMITTER} is not followed by a {@link ChainLinkType#RECEIVER} <li>A
 * {@link ChainLinkType#RECEIVER} is not preceded by a {@link ChainLinkType#TRANSMITTER}
 */
/**
 * A {@link Chain} of {@link ChainLinkType}s. A valid chain must obey the following rules:
 * <ul>
 * <li>a valid chain must not be empty
 * <li>The first or the last chain link must not be a {@link ChainLinkType#CONDITIONAL}
 * </ul>
 * <p>
 *
 * @author Michael Kuß
 */
/*
 * Tests can be found in {@link ChainSpec}.
 */
@EqualsAndHashCode
public class Chain implements Iterable<ChainLinkType> {
  private ChainLinkType[] chainLinks;

  /**
   * Private constructor - use {@link #of(ChainLinkType...)} to create a {@link Chain}
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
   *         <li>a {@link ChainLinkType#TRANSMITTER TRANSMITTER_RECEIVER} is not followed by a
   *         {@link ChainLinkType#RECEIVER RECEIVER}
   *         <li>A {@link ChainLinkType#RECEIVER RECEIVER} is followed by a
   *         {@link ChainLinkType#TRANSMITTER TRANSMITTER_RECEIVER}
   *         <li>A {@link ChainLinkType#RECEIVER RECEIVER} is not preceded by a
   *         {@link ChainLinkType#TRANSMITTER TRANSMITTER_RECEIVER}
   *         <li>The first or the last chain link is a {@link ChainLinkType#CONDITIONAL CONDITIONAL}
   *         </ul>
   */
  private Chain(ChainLinkType... chainLinks) {
    checkNotNull(chainLinks, "chainLinks must not be null");
    checkArgument(chainLinks.length > 0, "chain must not be empty");
    checkArgument(chainLinks[0] != CONDITIONAL, "chain must not start with a %s", CONDITIONAL);
    // checkArgument(chainLinks[chainLinks.length - 1] != CONDITIONAL,
    // "chain must not end with a %s", CONDITIONAL);
    for (int i = 0; i < chainLinks.length; i++) {
      checkNotNull(chainLinks[i], "chain link at index %s must not be null", i);
      // if (chainLinks[i] == TRANSMITTER) {
      // checkArgument(i + 1 < chainLinks.length && chainLinks[i + 1] == RECEIVER,
      // "%s at index %s is not followed by a %s", TRANSMITTER, i, RECEIVER);
      // }
      if (chainLinks[i] == RECEIVER) {
        // checkArgument(i - 1 >= 0 && chainLinks[i - 1] == TRANSMITTER,
        // "%s at index %s is not preceded by a %s", RECEIVER, i, TRANSMITTER);
        checkArgument(i + 1 >= chainLinks.length || chainLinks[i + 1] != TRANSMITTER,
            "%s at index %s is followed by a %s", RECEIVER, i, TRANSMITTER);
      }
    }
    this.chainLinks = new ChainLinkType[chainLinks.length];
    System.arraycopy(chainLinks, 0, this.chainLinks, 0, chainLinks.length);
  }

  /*
   * <li>a {@link ChainLinkType#TRANSMITTER TRANSMITTER_RECEIVER} is not followed by a {@link
   * ChainLinkType#RECEIVER RECEIVER} <li>A {@link ChainLinkType#RECEIVER RECEIVER} is not preceded
   * by a {@link ChainLinkType#TRANSMITTER TRANSMITTER_RECEIVER}
   */
  /**
   * Public factory method
   *
   * @param chainLinks - the {@link ChainLinkType}s to construct a new chain
   * @throws NullPointerException if
   *         <ul>
   *         <li>{@code chainLinks} is null
   *         <li>one of the elements of {@code chainLinks} is null
   *         </ul>
   * @throws IllegalArgumentException if
   *         <ul>
   *         <li>the array {@code chainLinks} is empty
   *         <li>A {@link ChainLinkType#RECEIVER RECEIVER} is followed by a
   *         {@link ChainLinkType#TRANSMITTER TRANSMITTER}
   *         <li>The first or the last chain link is a {@link ChainLinkType#CONDITIONAL CONDITIONAL}
   *         </ul>
   * @return a new {@link Chain} of the given {@link ChainLinkType}s
   */
  public static Chain of(ChainLinkType... chainLinks) {
    return new Chain(chainLinks);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + Arrays.asList(chainLinks).stream()
        .map((ChainLinkType c) -> c.name().substring(0, 1)).collect(Collectors.joining()) + ")";
  }

  /**
   * Returns the number of chain links in this chain.
   *
   * @return the number of chain links in this chain
   */
  public int size() {
    return chainLinks.length;
  }


  /**
   * Returns the first chain link of this chain.
   * <p>
   * Same as {@code get(0)}
   *
   * @return the first chain link of this chain
   */
  public ChainLinkType getFirstChainLink() {
    return chainLinks[0];
  }

  /**
   * Returns the chain link at the given index.
   *
   * @param index of the element to return
   * @return the chain link at the given index
   * @throws IndexOutOfBoundsException if {@code index} is out of bounds.
   */
  public ChainLinkType get(int index) {
    Preconditions.checkElementIndex(index, chainLinks.length, "chain link index");
    return chainLinks[index];
  }

  @Override
  public ListIterator<ChainLinkType> iterator() {
    return Arrays.asList(chainLinks).listIterator();
  }
}
