package com.jasper.creational.prototype;

import com.jasper.creational.prototype.career.Beast;
import com.jasper.creational.prototype.career.Mage;
import com.jasper.creational.prototype.elf.ElfBeast;
import com.jasper.creational.prototype.elf.ElfMage;
import com.jasper.creational.prototype.elf.ElfWarlord;
import com.jasper.creational.prototype.factory.HeroFactoryImpl;
import com.jasper.creational.prototype.orc.OrcBeast;
import com.jasper.creational.prototype.orc.OrcMage;
import com.jasper.creational.prototype.orc.OrcWarlord;

import lombok.extern.slf4j.Slf4j;

/**
 * App
 */
@Slf4j
public class App {
  public static void main(String[] args) {
    // cooking 冥想 准备中 cleaning 净化
    var factory = new HeroFactoryImpl(new ElfBeast("protecting"),
        new ElfMage("cooking"), new ElfWarlord("cleaning"));
    Beast beast = factory.createBeast();
    Mage mage = factory.createMage();
    var warlord = factory.createWarlord();
    log.info(beast.toString());
    log.info(mage.toString());
    log.info(warlord.toString());
    factory = new HeroFactoryImpl(new OrcBeast("axe"), new OrcMage("sword"), new OrcWarlord("laser"));
    mage = factory.createMage();
    warlord = factory.createWarlord();
    beast = factory.createBeast();
    log.info(mage.toString());
    log.info(warlord.toString());
    log.info(beast.toString());
  }
}
