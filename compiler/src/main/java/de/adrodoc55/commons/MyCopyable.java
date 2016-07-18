package de.adrodoc55.commons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.adrodoc55.commons.CopyScope.Copyable;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class MyCopyable implements Copyable {
  private int primitive;
  private String notCopyable;

  private MyCopyable copyable;
  private final List<MyCopyable> finalCopyableCollection = new ArrayList<>();
  private Set<MyCopyable> copyableCollection = new HashSet<>();

  private Object maybeCopyable;
  private final List<String> finalCollection = new ArrayList<>();
  private Set<Object> collection = new HashSet<>();

  protected MyCopyable(MyCopyable original, CopyScope scope) {
    primitive = original.primitive;
    notCopyable = original.notCopyable;

    copyable = scope.copy(original.copyable);
    finalCopyableCollection.addAll(scope.copy(original.finalCopyableCollection));
    copyableCollection = scope.copyInto(original.copyableCollection, new HashSet<>());

    maybeCopyable = scope.copyObject(original.maybeCopyable);
    finalCollection.addAll(scope.copyObjects(original.finalCollection));
    collection = scope.copyObjectsInto(original.collection, new HashSet<>());
  }

  @Override
  public Copyable copy(CopyScope scope) throws NullPointerException {
    return new MyCopyable(this, scope);
  }
}
