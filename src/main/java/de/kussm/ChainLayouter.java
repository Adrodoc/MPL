package de.kussm;

import static de.kussm.chain.ChainLink.CONDITIONAL;
import static de.kussm.chain.ChainLink.RECEIVER;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.kussm.chain.Chain;
import de.kussm.chain.ChainLink;
import de.kussm.direction.Direction;
import de.kussm.direction.Directions;
import de.kussm.position.Position;

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
  private LinkedHashMap<Position, ChainLink> placedChainLinks = Maps.newLinkedHashMap();
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
  private @Nullable ChainLink currentChainLink;

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

  private void restoreInsertionPoint() {
    if(insertionPoint==null) {
      System.out.println("hi");
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
    placedChainLinks.put(currentPosition, ChainLink.NO_OPERATION);
    currentPosition = currentPosition.neighbour(nextDirection);
    prevDirection = nextDirection;
    nextDirection = itDirections.next();
    cntDirections++;
  }


  private LinkedHashMap<Position, ChainLink> getPlacement() {
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
  }

  public static LinkedHashMap<Position, ChainLink> place(Chain chain, Directions dirs,
      Position startPosition, Predicate<Position> isReceiverAllowed,
      Predicate<Position> isTransmitterAllowed) {
    return new ChainLayouter(chain, dirs, startPosition, isReceiverAllowed, isTransmitterAllowed)
        .getPlacement();
  }

  public static LinkedHashMap<Position, ChainLink> place(Chain chain, Directions dirs,
      Predicate<Position> isReceiverAllowed, Predicate<Position> isTransmitterAllowed) {
    return place(chain, dirs, Position.at(0, 0), isReceiverAllowed, isTransmitterAllowed);
  }

  public static LinkedHashMap<Position, ChainLink> place(Chain chain, Directions dirs) {
    return place(chain, dirs, Position.at(0, 0), pos -> true, pos -> true);
  }

  public static LinkedHashMap<Position, ChainLink> place(Chain chain, Directions dirs,
      Position startPosition, Set<Position> forbiddenReceivers,
      Set<Position> forbiddenTransmitters) {
    return new ChainLayouter(chain, dirs, startPosition, pos -> !forbiddenReceivers.contains(pos),
        pos -> !forbiddenTransmitters.contains(pos)).getPlacement();
  }

}
