package de.adrodoc55.minecraft.mpl.commands.chainparts;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.ChainPartBuffer;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.IllegalModifierException;

/**
 * @author Adrodoc55
 */
@lombok.EqualsAndHashCode(of = {"not", "condition"})
@lombok.ToString(includeFieldNames = true, of = {"not", "condition"})
public class MplIf implements ChainPart, ChainPartBuffer {
  private final @Nullable ChainPartBuffer parent;
  private final boolean not;
  private final @Nonnull String condition;
  private final Deque<ChainPart> thenParts = new ArrayDeque<>();
  private final Deque<ChainPart> elseParts = new ArrayDeque<>();
  private boolean inElse;

  public MplIf(boolean not, @Nonnull String condition) {
    this(null, not, condition);
  }

  public MplIf(@Nullable ChainPartBuffer parent, boolean not, @Nonnull String condition) {
    this.parent = parent;
    this.not = not;
    this.condition = checkNotNull(condition, "condition == null!");
  }

  @Override
  public void add(ChainPart cp) {
    if (!inElse) {
      thenParts.add(cp);
    } else {
      elseParts.add(cp);
    }
  }

  @Override
  public Deque<ChainPart> getChainParts() {
    if (!inElse) {
      return thenParts;
    } else {
      return elseParts;
    }
  }

  public void enterThen() {
    inElse = false;
  }

  public void enterElse() {
    inElse = true;
  }

  public @Nullable ChainPartBuffer exit() {
    return parent;
  }

  @Override
  public String getName() {
    return "if";
  }

  @Override
  public List<? extends ChainLink> toCommands(CompilerOptions options)
      throws IllegalModifierException {
    // TODO Auto-generated method stub
    return null;
  }

}
