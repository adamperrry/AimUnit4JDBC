package com.aim.movie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.aim.movie.domain.Movie;
import com.aim.movie.util.IOUtils;
import com.aim.movie.util.MovieDBUtils;
import com.aim.movie.util.MySQL;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(MySQL.URL.value, MySQL.USER.value, MySQL.PASS.value)) {

            System.out.println("This program displays the Director, Genre and Rating of "
                    + "movies with the name supplied by a user.");

            String movieTitle = IOUtils.getMovieInput();

            List<Movie> movies = MovieDBUtils.getMovieList(connection, movieTitle);

            if (movies.size() > 0) {
                IOUtils.printMovies(movies);
            } else if (IOUtils.shouldAddMovie()) {
                Movie newMovie = Movie.createMovieFromUserInput();
                MovieDBUtils.insertMovie(connection, newMovie);
                movies = MovieDBUtils.getMovieList(connection, newMovie.getMovieTitle());
                IOUtils.printMovies(movies);
            } else {
                System.out.println("\nGoodbye...");
                return;
            }

            Movie movie = movies.get(0);

            String choice = IOUtils.updateOrDelete();

            if (choice == "update") {
                Movie.updateMovie(movie);
                MovieDBUtils.deleteMovie(connection, movie);
                MovieDBUtils.insertMovie(connection, movie);
                System.out.println(movie.getMovieTitle() + " has been updated.");
            } else if (choice == "delete") {
                MovieDBUtils.deleteMovie(connection, movie);
                System.out.println(movie.getMovieTitle() + " has been deleted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("\nGoodbye...");
    }

}
