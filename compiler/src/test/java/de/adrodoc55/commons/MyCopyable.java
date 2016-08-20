package de.adrodoc55.commons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.adrodoc55.commons.CopyScope.Copyable;

public class MyCopyable implements Copyable {
  private int primitive;
  private String notCopyable;
  private final MyCopyable finalCopyable;

  private MyCopyable nonFinalCopyable;
  private final List<MyCopyable> finalCopyableCollection = new ArrayList<>();
  private Set<MyCopyable> copyableCollection = new HashSet<>();

  private Object maybeCopyable;
  private final List<String> finalCollection = new ArrayList<>();
  private Set<Object> collection = new HashSet<>();

  @Deprecated
  protected MyCopyable(MyCopyable original, CopyScope scope) {
    primitive = original.primitive;
    notCopyable = original.notCopyable;
    finalCopyable = scope.copy(original.finalCopyable);
  }

  @Deprecated
  @Override
  public Copyable createFlatCopy(CopyScope scope) throws NullPointerException {
    return new MyCopyable(this, scope);
  }

  @Deprecated
  @Override
  public void completeDeepCopy(CopyScope scope) throws NullPointerException {
    MyCopyable original = scope.getCache().getOriginal(this);

    nonFinalCopyable = scope.copy(original.nonFinalCopyable);
    finalCopyableCollection.addAll(scope.copy(original.finalCopyableCollection));
    copyableCollection = scope.copyInto(original.copyableCollection, new HashSet<>());

    maybeCopyable = scope.copyObject(original.maybeCopyable);
    finalCollection.addAll(scope.copyObjects(original.finalCollection));
    collection = scope.copyObjectsInto(original.collection, new HashSet<>());
  }
}
