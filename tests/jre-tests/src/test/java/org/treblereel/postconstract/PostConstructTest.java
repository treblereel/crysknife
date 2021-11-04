/*
 * Copyright © 2021 Treblereel
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

package org.treblereel.postconstract;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.postconstruct.YetAnotherDependentBean;
import org.treblereel.postconstruct.YetAnotherSingletonBean;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/30/21
 */
public class PostConstructTest extends AbstractTest {

  @Test
  public void singletonTest() {
    for (int i = 0; i < 10; i++) {
      assertEquals(1,
          app.beanManager.lookupBean(YetAnotherSingletonBean.class).getInstance().getCounter());
    }
  }

  @Test
  public void dependentTest() {
    for (int i = 0; i < 10; i++) {
      assertEquals(1,
          app.beanManager.lookupBean(YetAnotherDependentBean.class).getInstance().getCounter());
    }
  }
}
