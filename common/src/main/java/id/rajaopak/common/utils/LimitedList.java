package id.rajaopak.common.utils;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class LimitedList<T> implements Collection<T> {

  private final int capacity;
  private final List<T> list;

  public LimitedList(int capacity) {
    this.capacity = capacity;
    this.list = new LinkedList<>();
  }

  @Override
  public boolean add(T element) {
    if (list.size() >= capacity) {
      list.remove(0);
    }
    return list.add(element);
  }

  @Override
  public boolean addAll(Collection<? extends T> collection) {
    boolean modified = false;
    for (T element : collection) {
      modified |= add(element);
    }
    return modified;
  }

  @Override
  public boolean remove(Object element) {
    return list.remove(element);
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> collection) {
    return list.removeAll(collection);
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> collection) {
    return list.retainAll(collection);
  }

  @Override
  public void clear() {
    list.clear();
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public boolean contains(Object element) {
    return list.contains(element);
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> collection) {
    return new HashSet<>(list).containsAll(collection);
  }

  @Override
  public Iterator<T> iterator() {
    return list.iterator();
  }

  @Override
  public Object[] toArray() {
    return list.toArray();
  }

  @Override
  public <E> E[] toArray(E @NotNull [] array) {
    return list.toArray(array);
  }

  @Override
  public String toString() {
    return list.toString();
  }
}
