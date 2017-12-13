package com.fri.musicbook;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.List;

@Entity(name = "artist")
@NamedQueries(value =
        {
                @NamedQuery(name = "com.fri.musicbook.Artist.getAll", query = "SELECT c FROM artist c")
        })
@UuidGenerator(name = "idGenerator")
public class Artist {

    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "genre_id")
    private String genreId;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }
}
