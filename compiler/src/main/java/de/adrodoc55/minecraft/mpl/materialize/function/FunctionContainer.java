package de.adrodoc55.minecraft.mpl.materialize.function;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FunctionContainer {
  private final McFunction rootFunction;
  private final Set<McFunction> functions = new HashSet<>();

  public FunctionContainer(McFunction rootFunction) {
    this.rootFunction = checkNotNull(rootFunction, "rootFunction == null!");
    functions.add(rootFunction);
  }

  public Set<McFunction> getFunctions() {
    return Collections.unmodifiableSet(functions);
  }

  public void addFunctions(Collection<McFunction> functions) {
    this.functions.addAll(functions);
  }
}
