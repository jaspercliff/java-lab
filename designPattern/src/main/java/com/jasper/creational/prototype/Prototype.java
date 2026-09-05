package com.jasper.creational.prototype;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Prototype<T> implements Cloneable {
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public T copy() {
    return (T) super.clone();
  }
}
