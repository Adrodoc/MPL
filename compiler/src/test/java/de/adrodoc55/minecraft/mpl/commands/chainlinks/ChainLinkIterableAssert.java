package de.adrodoc55.minecraft.mpl.commands.chainlinks;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.mpl.MplAssertionFactory.assertThat;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nullable;

import de.adrodoc55.commons.ExtendedAbstractAssert;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;

public class ChainLinkIterableAssert
    extends ExtendedAbstractAssert<ChainLinkIterableAssert, Iterable<? extends ChainLink>> {
  private final CompilerOptions options;

  public ChainLinkIterableAssert(@Nullable Iterable<? extends ChainLink> actual,
      CompilerOptions options) {
    super(actual, ChainLinkIterableAssert.class);
    this.options = checkNotNull(options, "options == null!");
  }

  public ChainLinkIterableAssert(@Nullable Iterator<? extends ChainLink> actual,
      CompilerOptions options) {
    this(toIterable(actual, options), options);
  }

  private static Iterable<? extends ChainLink> toIterable(
      @Nullable Iterator<? extends ChainLink> it, CompilerOptions options) {
    return new Iterable<ChainLink>() {
      private ChainLink[] links = new ChainLink[2];

      @Override
      public Iterator<ChainLink> iterator() {
        return new Iterator<ChainLink>() {
          private int nextIndex;

          @Override
          public ChainLink next() {
            nextIndex++;
            if (nextIndex == 1) {
              if (links[0] != null) {
                return links[0];
              } else {
                if (!it.hasNext())
                  throw new AssertionError(
                      "Expected actual to contain at least two elements, but found none");
                return links[0] = it.next();
              }
            } else if (nextIndex == 2) {
              if (links[1] != null) {
                return links[1];
              } else {
                if (!it.hasNext())
                  throw new AssertionError(
                      "Expected actual to contain at least two elements, but found only: "
                          + links[0]);
                return links[1] = it.next();
              }
            } else {
              throw new NoSuchElementException();
            }
          }

          @Override
          public boolean hasNext() {
            return nextIndex < 2;
          }
        };
      }
    };
  }

  public ChainLinkIterableAssert hasInternal(boolean internal) {
    assertThat(actual.iterator().next()).hasInternal(internal);
    return myself;
  }

  public ChainLinkIterableAssert isInternal() {
    assertThat(actual.iterator().next()).isInternal();
    return myself;
  }

  public ChainLinkIterableAssert isNotInternal() {
    assertThat(actual.iterator().next()).isNotInternal();
    return myself;
  }

  public ChainLinkIterableAssert isJumpDestination() {
    Iterator<? extends ChainLink> it = actual.iterator();
    if (options.hasOption(TRANSMITTER)) {
      assertThat(it.next()).isSkip();
      String trailer = options.hasOption(DEBUG) ? " air" : " stone";
      assertThat(it.next()).isInternal()//
          .hasCommandParts("setblock ", new RelativeThisInsert(-1), trailer)//
          .hasModifiers(modifier(IMPULSE))//
      ;
    } else {
      assertThat(it.next())//
          .hasCommandParts("blockdata ~ ~ ~ {auto:0b}")//
          .hasModifiers(modifier(IMPULSE))//
      ;
    }
    return myself;
  }
}
