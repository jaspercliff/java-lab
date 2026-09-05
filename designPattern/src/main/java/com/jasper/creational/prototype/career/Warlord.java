package com.jasper.creational.prototype.career;

import com.jasper.creational.prototype.Prototype;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Warlord 领袖
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class Warlord extends Prototype<Warlord> {
  public Warlord(Warlord source) {
  }
}
