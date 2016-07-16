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

import static de.kussm.chain.ChainLinkType.CONDITIONAL;
import static de.kussm.chain.ChainLinkType.RECEIVER;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import de.adrodoc55.minecraft.mpl.placement.NotEnoughSpaceException;
import de.kussm.direction.Direction;
import de.kussm.direction.Directions;
import de.kussm.position.Position;

/**
 * @author Michael Kuß
 */
public class ChainLayouter {
  private ChainLayouter(Chain chain, Directions dirs, Position startPosition,
      Predicate<Position> isReceiverAllowed, Predicate<Position> isTransmitterAllowed) {
    this.chain = chain;
    this.dirs = dirs;
    this.currentPosition = startPosition;
    this.isReceiverAllowed = isReceiverAllowed;
    this.isTransmitterAllowed = isTransmitterAllowed;
    this.itDirections = dirs.iterator();
    this.cntDirections = 1;
    this.prevDirection = null;
    this.nextDirection = itDirections.next();
    this.chainLinkIndex = 0;
    this.currentChainLink = chain.get(chainLinkIndex);
  }

  private final Chain chain;
  private final Directions dirs;
  private Position currentPosition;
  private Set<Position> placedReceivers = Sets.newHashSet();
  private Set<Position> placedTransmitters = Sets.newHashSet();
  private final Predicate<Position> isReceiverAllowed;
  private final Predicate<Position> isTransmitterAllowed;
  private Iterator<Direction> itDirections;
  private LinkedHashMap<Position, ChainLinkType> placedChainLinks = new LinkedHashMap<>();
  private int cntDirections;

  /**
   * The direction we are coming from
   */
  private Direction prevDirection;

  /**
   * The direction we are going to
   */
  private Direction nextDirection;

  /***
   * Index of current chain link in chain
   */
  private int chainLinkIndex;
  /**
   * Current chain link, Equals {@code null} if the end of the chain link is reached
   */
  private @Nullable ChainLinkType currentChainLink;

  /**
   * Restored point if NoOperations need to be inserted
   */
  private ChainLayouter insertionPoint;


  private void createInsertionPoint() {
    insertionPoint =
        new ChainLayouter(chain, dirs, currentPosition, isReceiverAllowed, isReceiverAllowed);
    insertionPoint.currentPosition = this.currentPosition;
    insertionPoint.placedReceivers.addAll(this.placedReceivers);
    insertionPoint.placedTransmitters.addAll(this.placedTransmitters);
    insertionPoint.cntDirections = this.cntDirections;
    insertionPoint.prevDirection = this.prevDirection;
    insertionPoint.nextDirection = this.nextDirection;
    insertionPoint.placedChainLinks.putAll(this.placedChainLinks);
    insertionPoint.chainLinkIndex = this.chainLinkIndex;
    insertionPoint.currentChainLink = this.currentChainLink;
  }

  private void restoreInsertionPoint() throws NotEnoughSpaceException {
    if (insertionPoint == null) {
      throw new NotEnoughSpaceException(
          "It is impossible to place the chain " + chain + " along the template " + dirs);
    }
    this.currentPosition = insertionPoint.currentPosition;
    this.placedReceivers = insertionPoint.placedReceivers;
    this.placedTransmitters = insertionPoint.placedTransmitters;
    this.cntDirections = insertionPoint.cntDirections;
    this.itDirections = dirs.iterator();
    Iterators.advance(this.itDirections, cntDirections);
    this.prevDirection = insertionPoint.prevDirection;
    this.nextDirection = insertionPoint.nextDirection;
    this.placedChainLinks = insertionPoint.placedChainLinks;
    this.chainLinkIndex = insertionPoint.chainLinkIndex;
    this.currentChainLink = insertionPoint.currentChainLink;
  }

  /**
   * Could we insert a NoOperation block before the current chain link?
   *
   * @return
   */
  private boolean insertionOfNoOperationIsPossible() {
    return chainLinkIndex != 0 && currentChainLink != RECEIVER && currentChainLink != CONDITIONAL;
  }

  /**
   * May the current chain link be placed here?
   *
   * @return
   */
  private boolean canPlaceChainLink() {
    switch (currentChainLink) {
      case TRANSMITTER:
        return isTransmitterAllowed.test(currentPosition)
            && Sets.intersection(currentPosition.neighbours(), placedReceivers).isEmpty();
      case RECEIVER:
        return isReceiverAllowed.test(currentPosition);
      // && Sets.intersection(currentPosition.neighbours(), placedTransmitters).isEmpty();
      case CONDITIONAL:
        return prevDirection == nextDirection;
      default:
        return true;
    }
  }

  private void placeChainLink() {
    switch (currentChainLink) {
      case TRANSMITTER:
        placedTransmitters.add(currentPosition);
        break;
      case RECEIVER:
        placedReceivers.add(currentPosition);
        break;
      default:
        break;
    }
    placedChainLinks.put(currentPosition, currentChainLink);
    chainLinkIndex++;
    currentChainLink = (chainLinkIndex < chain.size()) ? chain.get(chainLinkIndex) : null;
    currentPosition = currentPosition.neighbour(nextDirection);
    prevDirection = nextDirection;
    nextDirection = itDirections.next();
    cntDirections++;
  }


  private void insertNoOperation() {
    placedChainLinks.put(currentPosition, ChainLinkType.NO_OPERATION);
    currentPosition = currentPosition.neighbour(nextDirection);
    prevDirection = nextDirection;
    nextDirection = itDirections.next();
    cntDirections++;
  }


  private LinkedHashMap<Position, ChainLinkType> getPlacement() throws NotEnoughSpaceException {
    try {
      while (currentChainLink != null) {
        if (canPlaceChainLink()) {
          if (insertionOfNoOperationIsPossible()) {
            createInsertionPoint();
          }
          placeChainLink();
        } else {
          restoreInsertionPoint();
          insertNoOperation();
          createInsertionPoint();
        }
      }
      return placedChainLinks;
    } catch (NoSuchElementException ex) {
      throw new NotEnoughSpaceException(
          "End of template for chain " + chain + " (template=" + dirs + ")");
    }
  }

  public static LinkedHashMap<Position, ChainLinkType> place(Chain chain, Directions dirs,
      Position startPosition, Predicate<Position> isReceiverAllowed,
      Predicate<Position> isTransmitterAllowed) throws NotEnoughSpaceException {
    return new ChainLayouter(chain, dirs, startPosition, isReceiverAllowed, isTransmitterAllowed)
        .getPlacement();
  }

  public static LinkedHashMap<Position, ChainLinkType> place(Chain chain, Directions dirs,
      Predicate<Position> isReceiverAllowed, Predicate<Position> isTransmitterAllowed)
          throws NotEnoughSpaceException {
    return place(chain, dirs, Position.at(0, 0), isReceiverAllowed, isTransmitterAllowed);
  }

  public static LinkedHashMap<Position, ChainLinkType> place(Chain chain, Directions dirs)
      throws NotEnoughSpaceException {
    return place(chain, dirs, Position.at(0, 0), pos -> true, pos -> true);
  }

  public static LinkedHashMap<Position, ChainLinkType> place(Chain chain, Directions dirs,
      Position startPosition, Set<Position> forbiddenReceivers, Set<Position> forbiddenTransmitters)
          throws NotEnoughSpaceException {
    return new ChainLayouter(chain, dirs, startPosition, pos -> !forbiddenReceivers.contains(pos),
        pos -> !forbiddenTransmitters.contains(pos)).getPlacement();
  }

}
