package com.jasper.creational.prototype.career;

import com.jasper.creational.prototype.Prototype;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Mage 法师
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class Mage extends Prototype<Mage> {
  public Mage(Mage source) {
  }

}
