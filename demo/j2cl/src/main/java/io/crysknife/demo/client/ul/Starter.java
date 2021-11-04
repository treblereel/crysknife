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

package io.crysknife.demo.client.ul;

import elemental2.dom.DomGlobal;
import io.crysknife.client.ManagedInstance;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 10/28/21
 */
@Dependent
public class Starter {

    private ManagedInstance<UlElement> holder;
    private UlHolder ulHolder;

    @Inject
    Starter(ManagedInstance<UlElement> holder, UlHolder ulHolder) {
        this.holder = holder;
        this.ulHolder = ulHolder;
        DomGlobal.document.body.appendChild(ulHolder.getElement());
    }

    public void init() {
        for (int i = 0; i < 10; i++) {
            UlElement element = holder.get();
            element.init("name " +i);
            ulHolder.addElement(element.getElement());
        }
    }

}
