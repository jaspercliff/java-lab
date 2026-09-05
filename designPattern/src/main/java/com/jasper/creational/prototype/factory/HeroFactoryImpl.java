package com.jasper.creational.prototype.factory;

import com.jasper.creational.prototype.career.Beast;
import com.jasper.creational.prototype.career.Mage;
import com.jasper.creational.prototype.career.Warlord;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HeroFactoryImpl implements HeroFactory {

  private final Beast beast;
  private final Mage mage;
  private final Warlord warlord;

  @Override
  public Beast createBeast() {
    return beast.copy();
  }

  @Override
  public Mage createMage() {
    return mage.copy();
  }

  @Override
  public Warlord createWarlord() {
    return warlord.copy();
  }

}
