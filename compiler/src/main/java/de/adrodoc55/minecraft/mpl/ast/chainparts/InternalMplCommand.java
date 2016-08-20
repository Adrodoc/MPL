package de.adrodoc55.minecraft.mpl.ast.chainparts;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.commons.CopyScope.Copyable;
import de.adrodoc55.minecraft.mpl.ast.visitor.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;

public class InternalMplCommand implements ChainPart {

  private final ImmutableList<ChainLink> chainLinks;

  public InternalMplCommand(List<ChainLink> chainLinks) {
    this.chainLinks = ImmutableList.copyOf(chainLinks);
  }

  public InternalMplCommand(ChainLink... chainLinks) {
    this.chainLinks = ImmutableList.copyOf(chainLinks);
  }

  @Deprecated
  protected InternalMplCommand(InternalMplCommand original) {
    chainLinks = original.chainLinks;
  }

  @Override
  public Copyable createFlatCopy(CopyScope scope) throws NullPointerException {
    return new InternalMplCommand(this);
  }

  @Override
  public String getName() {
    return "internal command";
  }

  @Override
  public ImmutableList<ChainLink> accept(MplAstVisitor visitor) {
    return chainLinks;
  }

}
