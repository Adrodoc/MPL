package de.adrodoc55.minecraft.mpl;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.mpl.MplAssertionFactory.assertThat;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;

import java.util.Iterator;

import de.adrodoc55.commons.ExtendedAbstractAssert;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;

public class ChainLinkIterableAssert
    extends ExtendedAbstractAssert<ChainLinkIterableAssert, Iterator<? extends ChainLink>> {
  private final CompilerOptions options;

  public ChainLinkIterableAssert(Iterator<ChainLink> actual, CompilerOptions compilerOptions) {
    super(actual, ChainLinkIterableAssert.class);
    this.options = checkNotNull(compilerOptions, "compilerOptions == null!");
  }

  public ChainLinkIterableAssert isJumpDestination() {
    if (options.hasOption(TRANSMITTER)) {
      assertThat(actual.next()).isSkip();
      String trailer = options.hasOption(DEBUG) ? " air" : " stone";
      assertThat(actual.next()).isInternal()//
          .hasModifiers(modifier(IMPULSE))//
          .hasCommandParts("setblock ", new RelativeThisInsert(-1), trailer);
    } else {
      assertThat(actual.next()).isInternal()//
          .hasModifiers(modifier(IMPULSE))//
          .hasCommandParts("blockdata ~ ~ ~ {auto:0b}");
    }
    return myself;
  }
}
