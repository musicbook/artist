package com.fri.musicbook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class ArtistBean {

    @PersistenceContext(unitName = "artists-jpa")
    private EntityManager em;

    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    @PostConstruct
    private void init() {
        httpClient = HttpClientBuilder.create().build();
        objectMapper = new ObjectMapper();
        //objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @Inject
    @DiscoverService("albums")
    private String basePath;

    public String getAlbumPath(){
        return basePath;
    }

    public List<Artist> getArtists(){

        Query query = em.createNamedQuery("com.fri.musicbook.Artist.getAll", Artist.class);

        return query.getResultList();

    }

    public Artist getArtist(String artistId) {

        Artist artist = em.find(Artist.class, artistId);

        if (artist == null) {
            throw new NotFoundException();
        }
        artist.setAlbums(getAlbums(artistId));
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

    public List<Album> getAlbums(String artistId) {

        try {
            System.out.println("try"+basePath+"|..|"+artistId);
            HttpGet request = new HttpGet(basePath + "/v1/albums/artist/"+artistId);
            if (request==null)System.out.println("req je null");
            else System.out.println("req ni null");

            HttpResponse response = httpClient.execute(request);
            if (response==null)System.out.println("resp je null");
            else System.out.println("resp ni null");
            int status = response.getStatusLine().getStatusCode();
            System.out.println("stat je "+status);
            if (status >= 200 && status < 300) {
                System.out.println("v status");
                HttpEntity entity = response.getEntity();
                System.out.println(entity+"||"+entity.getContent()+"||"+entity.toString()+"||");
                if (entity != null) {
                    System.out.println(EntityUtils.toString(entity));
                    List<Album> albums = objectMapper.readValue(EntityUtils.toString(entity), new TypeReference<List<Album>>(){});
                    System.out.println("json:"+albums);
                    return albums;
                    //getObjects(EntityUtils.toString(entity));
                }
                System.out.println("ent=null");
            } else {
                System.out.println("else");
                String msg = "Remote server '" + basePath + "' is responded with status " + status + ".";
                //log.error(msg);
                throw new InternalServerErrorException(msg);
            }

        } catch (IOException e) {
            System.out.println("Exc"+e.getStackTrace()+
                    "\n"+e.getCause()+"\n"
                    +e.getLocalizedMessage()+"\n"
                    +e.getClass()+"\n"+e.getMessage());

            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            //log.error(msg);
            throw new InternalServerErrorException(msg);
        }

        return new ArrayList<>();

    }

}
