package de.adrodoc55.minecraft.mpl.ast;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.commons.CopyScope.Copyable;
import de.adrodoc55.minecraft.mpl.MplUtils;
import de.adrodoc55.minecraft.mpl.interpretation.VariableScope;
import de.adrodoc55.minecraft.mpl.interpretation.insert.Insert;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode
@ToString
public class CommandWithInserts implements Copyable {
  private final ImmutableList<Object> commandParts;

  public CommandWithInserts(String command) {
    this(ImmutableList.of(MplUtils.commandWithoutLeadingSlash(command)));
  }

  public CommandWithInserts(Iterable<?> commandParts) {
    this.commandParts = ImmutableList.copyOf(commandParts);
  }

  @Deprecated
  protected CommandWithInserts(CommandWithInserts original, CopyScope scope) {
    commandParts = ImmutableList.copyOf(scope.copyObjects(original.commandParts));
  }

  @Deprecated
  @Override
  public Copyable createFlatCopy(CopyScope scope) throws NullPointerException {
    return new CommandWithInserts(this, scope);
  }

  public String getCommand() {
    return Joiner.on("").join(commandParts);
  }

  public void resolve(VariableScope scope) {
    for (Object object : commandParts) {
      if (object instanceof Insert) {
        ((Insert) object).resolve(scope);
      }
    }
  }
}
