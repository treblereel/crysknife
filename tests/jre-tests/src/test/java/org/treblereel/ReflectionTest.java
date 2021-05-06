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

package org.treblereel;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class ReflectionTest {

  @Test
  public void simpleTest() throws NoSuchFieldException, IllegalAccessException,
      InvocationTargetException, NoSuchMethodException {
    SimpleClass tested = new SimpleClass();

    FieldUtils.readField(tested, "testField", true);
    assertNull(FieldUtils.readField(tested, "testField", true));
    FieldUtils.writeField(tested, "testField", "Value", true);
    assertNotNull(FieldUtils.readField(tested, "testField", true));
    assertEquals("Value", FieldUtils.readField(tested, "testField", true));
    MethodUtils.invokeMethod(tested, true, "init");
    assertTrue(tested.initCalled);
  }


  private static class SimpleClass {

    private String testField;

    private boolean initCalled = false;

    private void init() {
      this.initCalled = true;
    }

    public String getTestField() {
      return testField;
    }

    public void setTestField(String testField) {
      this.testField = testField;
    }
  }
}
