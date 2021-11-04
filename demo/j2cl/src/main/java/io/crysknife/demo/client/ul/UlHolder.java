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

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.Node;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 10/28/21
 */
@Dependent
@Templated
public class UlHolder implements IsElement {

    @Inject
    @DataField("kie-palette")
    private HTMLDivElement palette;

    @Inject
    @DataField("list-group")
    private HTMLUListElement ul;

    public final void addElement(Node widget) {
        ul.appendChild(widget);
    }
}
