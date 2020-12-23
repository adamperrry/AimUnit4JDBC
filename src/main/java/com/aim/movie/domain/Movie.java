package com.aim.movie.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aim.movie.util.Input;

public class Movie {

    private int id;
    private String movieTitle;
    private int movieLength;
    private Date releaseDate;
    private String genre;
    private String rating;
    private Director director;
    private List<Actor> actors;

    private static String prompt;
    private static Input input = Input.getInstance();

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

    // create a movie from user input
    public static Movie createMovieFromUserInput() {
        Movie newMovie = new Movie();

        System.out.println("\nPlease enter the information for the movie.");
        prompt = "Movie title: ";
        newMovie.setMovieTitle(input.getString(prompt, true));

        prompt = "Movie length (min): ";
        newMovie.setMovieLength(input.getInt(prompt));

        prompt = "Release Date (yyyy-mm-dd): ";
        newMovie.setReleaseDate(input.getDate(prompt, "yyyy-MM-dd"));

        prompt = "Genre: ";
        newMovie.setGenre(input.getString(prompt));

        prompt = "Rating (G, PG, PG-13, R): ";
        newMovie.setRating(input.getString(prompt));

        prompt = "Director: ";
        Director director = new Director();
        director.setFullName(input.getString(prompt));
        newMovie.setDirector(director);

        prompt = "Actor: ";
        Actor actor = new Actor();
        actor.setFullName(input.getString(prompt));
        List<Actor> actors = new ArrayList<>(List.of(actor));
        newMovie.setActors(actors);

        System.out.print("\nThanks for adding a movie to the database!\n");
        return newMovie;
    }

    public static void updateMovie(Movie movie) {
        String defaultTitle = movie.getMovieTitle();
        prompt = "Change movie title (" + defaultTitle + "):";
        String newMovieTitle = input.getStringOrDefault(prompt, defaultTitle);
        movie.setMovieTitle(newMovieTitle);

        int defaultLength = movie.getMovieLength();
        prompt = "Change movie length (min) (" + defaultLength + "):";
        int newMovieLength = input.getIntOrDefault(prompt, defaultLength);
        movie.setMovieLength(newMovieLength);

        Date defaultDate = movie.getReleaseDate();
        prompt = "Change movie release date (yyyy-mm-dd) (" + defaultDate + "):";
        Date newReleaseDate = input.getDateOrDefault(prompt, "yyyy-MM-dd", defaultDate);
        movie.setReleaseDate(newReleaseDate);

        String defaultGenre = movie.getGenre();
        prompt = "Change movie genre (" + defaultGenre + "):";
        String newMovieGenre = input.getStringOrDefault(prompt, defaultGenre);
        movie.setGenre(newMovieGenre);

        String defaultRating = movie.getRating();
        prompt = "Change movie rating (" + defaultRating + "):";
        String newMovieRating = input.getStringOrDefault(prompt, defaultRating);
        movie.setRating(newMovieRating);

        String defaultDirector = movie.getDirector().getFullName();
        prompt = "Change movie director (" + defaultDirector + "):";
        String newMovieDirector = input.getStringOrDefault(prompt, defaultDirector);
        Director director = new Director();
        director.setFullName(newMovieDirector);
        movie.setDirector(director);

        String defaultActor = movie.getActors().get(0).getFullName();
        prompt = "Change movie actor (" + defaultActor + "):";
        String newMovieActor = input.getStringOrDefault(prompt, defaultActor);
        Actor actor = new Actor();
        actor.setFullName(newMovieActor);
        List<Actor> actors = new ArrayList<>(List.of(actor));
        movie.setActors(actors);
    }

}
