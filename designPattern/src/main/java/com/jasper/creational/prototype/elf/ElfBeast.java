package com.jasper.creational.prototype.elf;

import com.jasper.creational.prototype.career.Beast;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * ElfBeast
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ElfBeast extends Beast {
  private final String helpType;

  @Override
  public String toString() {
    return "eleven eagle helps in " + helpType;
  }

}
