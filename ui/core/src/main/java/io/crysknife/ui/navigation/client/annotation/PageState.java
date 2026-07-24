/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package io.crysknife.ui.navigation.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated field holds information about the state of the current page. The
 * navigation framework writes state information from the history token to the field when navigating
 * to the page, before {@code @PageShowing} is called.
 * <p>
 * The target field must not be {@code private} or {@code final}. Supported field types:
 * {@code String}, {@code int}/{@code Integer}, {@code long}/{@code Long},
 * {@code boolean}/{@code Boolean}, {@code double}/{@code Double}, {@code float}/{@code Float},
 * {@code short}/{@code Short}, {@code byte}/{@code Byte},
 * {@code List<String>}, {@code List<Integer>}, {@code List<Long>}, {@code List<Boolean>},
 * {@code List<Double>}, {@code List<Float>}, {@code List<Short>}, {@code List<Byte>}.
 * <p>
 * For {@code List} fields, all URL parameter values are collected into the list.
 * {@code defaultValue} is not supported for {@code List} fields.
 *
 * @see Page
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface PageState {

  String DEFAULT_VALUE_UNSET = "\0";

  /**
   * Provides a way to map the field name to a different value for the query parameter. If not
   * specified, the name of the field will be used as the name of the query parameter.
   */
  String value() default "";

  /**
   * Provides a default value for the field when the query parameter is not present in the URL.
   * If not specified and the parameter is absent, the field is not modified.
   */
  String defaultValue() default DEFAULT_VALUE_UNSET;

}
