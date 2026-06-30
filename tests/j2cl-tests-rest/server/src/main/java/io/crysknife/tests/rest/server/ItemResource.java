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

package io.crysknife.tests.rest.server;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.crysknife.tests.rest.shared.model.Item;

@Path("/api/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    @GET
    @Path("/{id}")
    public Item getItem(@PathParam("id") long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("item-" + id);
        return item;
    }

    @GET
    public List<Item> listItems() {
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("item-1");
        items.add(item1);
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("item-2");
        items.add(item2);
        return items;
    }

    @POST
    public Response createItem(Item item) {
        return Response.status(Response.Status.CREATED).entity(item).build();
    }
}
