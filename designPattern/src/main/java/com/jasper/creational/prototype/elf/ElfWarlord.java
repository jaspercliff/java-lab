
package com.jasper.creational.prototype.elf;

import com.jasper.creational.prototype.career.Warlord;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * ElfBeast
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ElfWarlord extends Warlord {
  private final String helpType;

  @Override
  public String toString() {
    return "eleven Warlord helps in " + helpType;
  }

}
