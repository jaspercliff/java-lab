package com.jasper.creational.prototype.factory;

import com.jasper.creational.prototype.career.Beast;
import com.jasper.creational.prototype.career.Mage;
import com.jasper.creational.prototype.career.Warlord;

public interface HeroFactory {

  public Beast createBeast();

  public Mage createMage();

  public Warlord createWarlord();

}
