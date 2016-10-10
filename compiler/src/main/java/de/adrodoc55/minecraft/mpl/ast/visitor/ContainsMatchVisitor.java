package de.adrodoc55.minecraft.mpl.ast.visitor;

import java.util.function.Predicate;

import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.InternalMplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCall;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreak;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinue;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.interpretation.ChainPartBuffer;

public class ContainsMatchVisitor implements MplAstVisitor<Boolean> {
  private final Predicate<? super ChainPart> prediacate;

  public ContainsMatchVisitor(Predicate<? super ChainPart> prediacate) {
    this.prediacate = prediacate;
  }

  /**
   * Returns {@code true} if the specified {@link ChainPart} or any of it's children match the
   * {@link Predicate}, false otherwise.
   *
   * @param <C> the type of {@link ChainPartBuffer}
   * @param chainPartBuffer
   * @return {@code true} if all elements match the predicate
   */
  public <C extends Object & ChainPartBuffer & ChainPart> Boolean test(C chainPartBuffer) {
    if (test((ChainPart) chainPartBuffer)) {
      return true;
    }
    return test((ChainPartBuffer) chainPartBuffer);
  }

  /**
   * Returns {@code true} if any {@link ChainPart} of the specified {@link ChainPartBuffer} matches
   * the {@link Predicate}, false otherwise.
   *
   * @param chainPartBuffer
   * @return {@code true} if all elements match the predicate
   */
  public Boolean test(ChainPartBuffer chainPartBuffer) {
    return test(chainPartBuffer.getChainParts());
  }

  /**
   * Returns {@code true} if any {@link ChainPart} of the specified {@link Iterable} matches the
   * {@link Predicate}, false otherwise.
   *
   * @param chainParts
   * @return {@code true} if all elements match the predicate
   */
  public Boolean test(Iterable<ChainPart> chainParts) {
    for (ChainPart chainPart : chainParts) {
      if (chainPart.accept(this)) {
        return true;
      }
    }
    return false;
  }

  private Boolean test(ChainPart chainPart) {
    return prediacate.test(chainPart);
  }

  @Override
  public Boolean visitInternalCommand(InternalMplCommand mplCommand) {
    return test(mplCommand);
  }

  @Override
  public Boolean visitCommand(MplCommand mplCommand) {
    return test(mplCommand);
  }

  @Override
  public Boolean visitCall(MplCall mplCall) {
    return test(mplCall);
  }

  @Override
  public Boolean visitStart(MplStart mplStart) {
    return test(mplStart);
  }

  @Override
  public Boolean visitStop(MplStop mplStop) {
    return test(mplStop);
  }

  @Override
  public Boolean visitWaitfor(MplWaitfor mplWaitfor) {
    return test(mplWaitfor);
  }

  @Override
  public Boolean visitNotify(MplNotify mplNotify) {
    return test(mplNotify);
  }

  @Override
  public Boolean visitIntercept(MplIntercept mplIntercept) {
    return test(mplIntercept);
  }

  @Override
  public Boolean visitBreakpoint(MplBreakpoint mplBreakpoint) {
    return test(mplBreakpoint);
  }

  @Override
  public Boolean visitSkip(MplSkip mplSkip) {
    return test(mplSkip);
  }

  @Override
  public Boolean visitIf(MplIf mplIf) {
    if (test((ChainPart) mplIf)) {
      return true;
    }
    if (test(mplIf.getThenParts())) {
      return true;
    }
    if (test(mplIf.getElseParts())) {
      return true;
    }
    return false;
  }

  @Override
  public Boolean visitWhile(MplWhile mplWhile) {
    return test(mplWhile);
  }

  @Override
  public Boolean visitBreak(MplBreak mplBreak) {
    return test(mplBreak);
  }

  @Override
  public Boolean visitContinue(MplContinue mplContinue) {
    return test(mplContinue);
  }
}
