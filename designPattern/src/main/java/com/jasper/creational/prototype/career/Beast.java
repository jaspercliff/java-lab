package com.jasper.creational.prototype.career;

import com.jasper.creational.prototype.Prototype;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 野兽/坐骑
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class Beast extends Prototype<Beast> {
  public Beast(Beast source) {
  }

}
