package com.fri.musicbook;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import com.fri.musicbook.ArtistBean;
import com.fri.musicbook.*;

@RequestScoped
@Path("/artists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistResource {

    @Context
    protected UriInfo uriInfo;

    @Inject
    private ArtistBean artistBean;

    @GET
    public Response getArtists() {

        List<Artist> artists = artistBean.getArtists();

        return Response.ok(artists).build();
    }

    @GET
    @Path("/{artistId}")
    public Response getArtist(@PathParam("artistId") String artistId) {

        Artist artist = artistBean.getArtist(artistId);

        if (artist == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(artist).build();
    }

    @GET
    @Path("/filtered")
    public Response getArtistsFiltered() {

        List<Artist> artists;

        artists = artistBean.getArtistsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(artists).build();
    }
    
    @POST
    public Response createArtist(Artist artist) {

        if ((artist.getName() == null || artist.getName().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            artist = artistBean.createArtist(artist);
        }

        if (artist.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(artist).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(artist).build();
        }
    }

    @PUT
    @Path("{artistId}")
    public Response putArtist(@PathParam("artistId") String artistId, Artist artist) {

        artist = artistBean.putArtist(artistId, artist);

        if (artist == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (artist.getId() != null)
                return Response.status(Response.Status.OK).entity(artist).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{artistId}")
    public Response deleteArtist(@PathParam("artistId") String artistId) {

        boolean deleted = artistBean.deleteArtist(artistId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}