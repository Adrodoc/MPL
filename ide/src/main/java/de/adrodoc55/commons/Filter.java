package de.adrodoc55.commons;

public interface Filter<T> {
  public static final Filter<?> ALL = t -> true;
  public static final Filter<?> NONE = t -> false;

  @SuppressWarnings("unchecked")
  public static <T> Filter<T> all() {
    return (Filter<T>) ALL;
  }

  @SuppressWarnings("unchecked")
  public static <T> Filter<T> none() {
    return (Filter<T>) NONE;
  }

  boolean matches(T subject);
}
