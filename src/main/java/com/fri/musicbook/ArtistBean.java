package com.fri.musicbook;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@RequestScoped
public class ArtistBean {

    @PersistenceContext(unitName = "artists-jpa")
    private EntityManager em;

    public List<Artist> getArtists(){

        Query query = em.createNamedQuery("com.fri.musicbook.Artist.getAll", Artist.class);

        return query.getResultList();

    }

    public Artist getArtist(String artistId) {

        Artist artist = em.find(Artist.class, artistId);

        if (artist == null) {
            throw new NotFoundException();
        }

        return artist;
    }

    public List<Artist> getArtistsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        List<Artist> artists = JPAUtils.queryEntities(em, Artist.class, queryParameters);

        return artists;
    }

    public Artist createArtist(Artist artist) {

        try {
            beginTx();
            em.persist(artist);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return artist;
    }

    public Artist putArtist(String artistId, Artist artist) {

        Artist c = em.find(Artist.class, artistId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            artist.setId(c.getId());
            artist = em.merge(artist);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return artist;
    }

    public boolean deleteArtist(String artistId) {

        Artist artist = em.find(Artist.class, artistId);

        if (artist != null) {
            try {
                beginTx();
                em.remove(artist);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

}
