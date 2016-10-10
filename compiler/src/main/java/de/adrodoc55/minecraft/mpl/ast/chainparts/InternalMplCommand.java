package de.adrodoc55.minecraft.mpl.ast.chainparts;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.commons.CopyScope.Copyable;
import de.adrodoc55.minecraft.mpl.ast.visitor.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer;

public class InternalMplCommand extends ModifiableChainPart {
  private final ImmutableList<Command> chainLinks;

  public InternalMplCommand(MplSource source, List<? extends Command> chainLinks) {
    super(new ModifierBuffer(), source);
    checkArgument(!chainLinks.isEmpty(), "chainlinks must not be empty!");
    this.chainLinks = ImmutableList.copyOf(chainLinks);
  }

  public InternalMplCommand(MplSource source, Command... chainLinks) {
    this(source, Arrays.asList(chainLinks));
  }

  @Deprecated
  protected InternalMplCommand(InternalMplCommand original, CopyScope scope) {
    super(original);
    chainLinks = ImmutableList.copyOf(scope.copy(original.chainLinks));
  }

  @Deprecated
  @Override
  public Copyable createFlatCopy(CopyScope scope) throws NullPointerException {
    return new InternalMplCommand(this, scope);
  }

  @Override
  public String getName() {
    return "internal command";
  }

  public ImmutableList<ChainLink> getChainLinks() {
    Command first = chainLinks.get(0);
    first.setModifier(this);
    return ImmutableList.copyOf(chainLinks);
  }

  @Override
  public <T> T accept(MplAstVisitor<T> visitor) {
    return visitor.visitInternalCommand(this);
  }

}
