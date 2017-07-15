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
package de.adrodoc55.minecraft.mpl.commands.chainlinks;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nullable;

import org.assertj.core.api.AbstractBooleanAssert;

import de.adrodoc55.commons.ExtendedAbstractAssert;
import de.adrodoc55.minecraft.mpl.MplAssertionFactory;
import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;

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

  private ChainLinkAssert<?, ChainLink> assertThat(ChainLink chainLink) {
    return MplAssertionFactory.assertThat(chainLink, options);
  }

  public ChainLinkIterableAssert matches(MplCommand expected, Mode previousMode) {
    Iterator<? extends ChainLink> iterator = actual.iterator();
    if (expected.getConditional() == Conditional.INVERT) {
      assertThat(iterator.next()).isInvertingCommandFor(previousMode);
    }
    assertThat(iterator.next()).matches(expected);
    return myself;
  }

  public ChainLinkIterableAssert isJumpDestination() {
    Iterator<? extends ChainLink> it = actual.iterator();
    if (options.hasOption(TRANSMITTER)) {
      assertThat(it.next()).isSkip();
      assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(modifier(IMPULSE));
    } else {
      assertThat(it.next()).isStopCommand().hasModifiers(modifier(IMPULSE));
    }
    return myself;
  }

  public ChainLinkIterableAssert isUnconditionalNotify(String event) {
    ProcessCommandsHelper helper = new ProcessCommandsHelper(options);
    Iterator<? extends ChainLink> it = actual.iterator();
    assertThat(it.next())
        .hasCommandParts(
            "execute @e[name=" + event + NOTIFY + "] ~ ~ ~ " + helper.getStartCommand())
        .hasModifiers(modifier());
    assertThat(it.next()).isInternal().hasCommandParts("kill @e[name=" + event + NOTIFY + "]")
        .hasModifiers(modifier());
    return myself;
  }

  public AbstractBooleanAssert<?> internal() {
    return assertThat(actual.iterator().next()).internal();
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

  public ChainLinkIterableAssert isSkip() {
    assertThat(actual.iterator().next()).isSkip();
    return myself;
  }

  public ChainLinkIterableAssert hasMode(Mode mode) {
    assertThat(actual.iterator().next()).hasMode(mode);
    return myself;
  }

  public ChainLinkIterableAssert doesNeedRedstone() {
    assertThat(actual.iterator().next()).doesNeedRedstone();
    return myself;
  }
}
