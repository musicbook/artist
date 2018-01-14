package com.fri.musicbook;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.List;

@Entity(name = "albums")
@NamedQueries(value =
        {
                @NamedQuery(name = "albums.getAll", query = "SELECT o FROM albums o"),
                @NamedQuery(name = "albums.getAlbumsByArtist", query = "SELECT o FROM albums o WHERE o.artistId = :artistId")
        })
@UuidGenerator(name = "idGenerator")
public class Album {
    //private String year_of_relese;12
    @Column(name = "album_name")
    private String albumName;

    @Column(name="artist_id")
    private Integer artistId;

    //private String genre;
    @Id
    @Column(name = "album_id")
    private Integer albumId;

    @Transient
    private String songs;

    // Sets/Gets

   // public List<Song> getSongs() {return songs;}

   // public void setSongs(List<Song> songs) {this.songs=songs;}

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer AlbumId) {
        this.albumId = AlbumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String name) {
        this.albumName = name;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artist) {
        this.artistId = artist;
    }

    public String getSongs() {
        return songs;
    }

    public void setSongs(String songs) {
        this.songs = songs;
    }
/*
    public int getYear_of_relese(){
        return year_of_relese;
    }
    public void setYear_of_relese(String year_of_relese){
        this.year_of_relese=year_of_relese;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
*/


}
