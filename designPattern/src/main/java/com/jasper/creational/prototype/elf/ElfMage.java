package com.jasper.creational.prototype.elf;

import com.jasper.creational.prototype.career.Mage;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * ElfBeast
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ElfMage extends Mage {
  private final String helpType;

  @Override
  public String toString() {
    return "eleven mage helps in " + helpType;
  }

}
