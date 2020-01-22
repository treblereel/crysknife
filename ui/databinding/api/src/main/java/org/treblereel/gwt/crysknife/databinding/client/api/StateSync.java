/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.treblereel.gwt.crysknife.databinding.client.api;

/**
 * Specifies the state from which a {@link DataBinder}'s properties should be initialized.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
public enum StateSync {
  
  /**
   * Specifies that the bound value should be initialized to the pre-existing
   * value in the model.
   */
  FROM_MODEL {
    @Override
    public <T> T getInitialValue(T modelValue, T widgetValue) {
      return modelValue;
    }
  },
  
  /**
   * Specifies that the bound value should be initialized to the pre-existing
   * value in the UI widget.
   */
  FROM_UI {
    @Override
    public <T> T getInitialValue(T modelValue, T widgetValue) {
      return widgetValue;
    }
  };
  
  /**
   * Returns the model value or the UI value, as appropriate.
   * 
   * @param modelValue
   *          The pre-existing model value.
   * @param widgetValue
   *          The pre-existing UI widget value.
   * @return Either model or widget. Return value will be null if the
   *         corresponding parameter value is null.
   */
  public abstract <T> T getInitialValue(T modelValue, T widgetValue);
}