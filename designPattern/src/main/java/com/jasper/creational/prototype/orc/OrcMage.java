package com.jasper.creational.prototype.orc;

import com.jasper.creational.prototype.career.Mage;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * ElfBeast
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrcMage extends Mage {
  private final String weapon;

  @Override
  public String toString() {
    return "eleven mage helps in " + weapon;
  }

}
