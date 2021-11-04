/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


package org.treblereel.injection.cycle.postconstruct;

import io.crysknife.annotation.CircularDependency;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/30/21
 */
@Singleton
@CircularDependency
public class BeanTwo {

  private BeanThree three;

  private int id = new Random().nextInt();


  public BeanThree getThree() {
    return three;
  }

  private AtomicInteger atomicInteger = new AtomicInteger();

  @Inject
  public BeanTwo(BeanThree three) {
    this.three = three;

  }

  @PostConstruct
  public void init() {
    atomicInteger.incrementAndGet();
  }

  public void say() {}

  public AtomicInteger getAtomicInteger() {
    return atomicInteger;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof BeanTwo))
      return false;

    BeanTwo beanOne = (BeanTwo) o;

    return id == beanOne.id;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
