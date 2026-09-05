package com.jasper.creational.prototype.orc;

import com.jasper.creational.prototype.career.Warlord;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * ElfBeast
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrcWarlord extends Warlord {
  private final String weapon;

  @Override
  public String toString() {
    return "eleven Warlord helps in " + weapon;
  }

}
