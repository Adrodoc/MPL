package de.adrodoc55.minecraft.mpl.commands.chainparts;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.ChainPartBuffer;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.IllegalModifierException;

public class MplIf implements ChainPart {
  private ChainPartBuffer parent;
  private final boolean not;
  private final @Nonnull String condition;

  public MplIf(ChainPartBuffer parent, boolean not, @Nonnull String condition) {
    this.parent = parent;
    this.not = not;
    this.condition = checkNotNull(condition, "condition == null!");
  }

  private final LinkedList<ChainPart> thenBlock = new LinkedList<>();
  private boolean inElse = false;

  public boolean needsNormalizer() {
    if (not) {
      // Muss nicht iterieren, da der Erste nicht conditional sein kann und einmal nicht
      // conditional ausreicht.
      return !getElseBlock().isEmpty();
    } else {
      Iterator<ChainPart> it = getThenBlock().iterator();
      if (it.hasNext()) {
        it.next(); // Ignore the first element.
      }
      while (it.hasNext()) {
        ChainPart chainPart = it.next();
        if (!(chainPart instanceof Command)) {
          // TODO: Besseren Fehler werfen
          throw new IllegalStateException("Skip is not allowed within if body!");
        }
        Command command = (Command) chainPart;
        if (!command.isConditional()) {
          return true;
        }
      }
      return false;
    }
  }

  public void switchToElseBlock() {
    thenBlock.addAll(chainParts);
    chainParts.clear();
    inElse = true;
  }

  public List<ChainPart> getThenBlock() {
    if (!inElse) {
      // If we are still editing the then block and the thenBlock is not already refreshed,
      // refresh the elements.
      if (!thenBlock.equals(chainParts)) {
        thenBlock.clear();
        thenBlock.addAll(chainParts);
      }
    }
    return Collections.unmodifiableList(thenBlock);
  }

  public List<ChainPart> getElseBlock() {
    if (inElse) {
      return Collections.unmodifiableList(chainParts);
    } else {
      return new LinkedList<>();
    }
  }


  @Override
  public List<? extends ChainLink> toCommands(CompilerOptions options)
      throws IllegalModifierException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Mode getModeToInvert() throws IllegalModifierException {
    throw new IllegalModifierException("Cannot depend on intercept");
  }

}
