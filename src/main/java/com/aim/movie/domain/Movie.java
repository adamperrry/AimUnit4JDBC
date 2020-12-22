package com.aim.movie.domain;

import java.util.Date;
import java.util.List;

public class Movie {

    private int id;
    private String movieTitle;
    private int movieLength;
    private Date releaseDate;
    private String genre;
    private String rating;
    private Director director;
    private List<Actor> actors;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getMovieLength() {
        return movieLength;
    }

    public void setMovieLength(int movieLength) {
        this.movieLength = movieLength;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public String toString() {
        StringBuilder toString = new StringBuilder();
        toString.append("Title=" + getMovieTitle());
        toString.append(", Length=" + getMovieLength());
        toString.append(", Genre=" + getGenre());
        toString.append(", Rating=" + getRating());
        if (getDirector() != null) {
            toString.append(", Director: " + getDirector());
        }
        if (getActors() != null & getActors().size() > 0) {
            toString.append(", Actors:" + getActors());
        }
        return toString.toString();
    }

}
