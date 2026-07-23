/*
 * Copyright © 2026 Treblereel
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

package io.crysknife.demo.client.rest;

import java.util.List;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.treblereel.gwt.rest.client.Caller;

@Singleton
@Page
@Templated("restdemo.html")
public class RestDemo implements IsElement<HTMLDivElement> {

    @Inject
    @Named("jsonplaceholder")
    Caller<PostService> postServiceCaller;

    @Inject
    @DataField
    HTMLDivElement root;

    @Inject
    @DataField
    HTMLButtonElement getPostBtn;

    @Inject
    @DataField
    HTMLInputElement getPostResult;

    @Inject
    @DataField
    HTMLButtonElement listPostsBtn;

    @Inject
    @DataField
    HTMLInputElement listPostsResult;

    @EventHandler("getPostBtn")
    public void onGetPost(@ForEvent("click") MouseEvent e) {
        postServiceCaller
            .onError((response, throwable) -> {
                getPostResult.value = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                Post post = (Post) r;
                getPostResult.value = post.getId() + ": " + post.getTitle();
            })
            .getPost(1);
    }

    @EventHandler("listPostsBtn")
    @SuppressWarnings("unchecked")
    public void onListPosts(@ForEvent("click") MouseEvent e) {
        postServiceCaller
            .onError((response, throwable) -> {
                listPostsResult.value = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                List<Post> posts = (List<Post>) r;
                listPostsResult.value = posts.size() + " posts loaded";
            })
            .listPosts();
    }

    @Override
    public HTMLDivElement getElement() {
        return root;
    }
}
