package com.jasper.creational.prototype.orc;

import com.jasper.creational.prototype.career.Beast;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * ElfBeast
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrcBeast extends Beast {
  private final String weapon;

  @Override
  public String toString() {
    return "oraish walf attack with " + weapon;
  }

}
