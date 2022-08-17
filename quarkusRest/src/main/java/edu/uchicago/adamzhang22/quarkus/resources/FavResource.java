package edu.uchicago.adamzhang22.quarkus.resources;


import edu.uchicago.adamzhang22.quarkus.models.Fav;
import edu.uchicago.adamzhang22.quarkus.services.FavsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/favs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FavResource {


    @Inject
    FavsService favsService;


    @POST
    public Fav add(Fav favoriteItem){
        return favsService.add(favoriteItem);
    }

    @GET
    @Path("{id}")
    public Fav get(@PathParam("id") String id) {

        return favsService.get(id);
    }

    @DELETE
    @Path("{id}")
    public Fav delete(@PathParam("id") String id) {
        return favsService.delete(id);
    }

    @PUT
    @Path("{id}")
    public Fav update(@PathParam("id") String id, Fav fav) {
        return favsService.update(id, fav);
    }

    @GET
    @Path("/paged/{userEmail}/{page}")
    public List<Fav> paged(@PathParam("userEmail") String userEmail, @PathParam("page") int page){
        return  favsService.paged(userEmail, page);
    }




}