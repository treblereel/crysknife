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

package io.crysknife.demo.client.llop;

import elemental2.dom.DomGlobal;
import io.crysknife.annotation.CircularDependency;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 10/30/21
 */
@Singleton
@CircularDependency
public class BeanOne {

    private BeanTwo two;

    @Inject
    BeanOne(BeanTwo two) {
        this.two = two;
    }

    @PostConstruct
    public void init() {
        DomGlobal.console.log("init " + getClass().getCanonicalName());
    }

    public void say() {
        DomGlobal.console.log("BeanOne say " + two.getClass().getCanonicalName());

    }
}
