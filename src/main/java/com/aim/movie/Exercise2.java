package com.aim.movie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.aim.movie.domain.Actor;
import com.aim.movie.domain.Director;
import com.aim.movie.domain.Movie;
import com.aim.movie.util.MySQL;

public class Exercise2 {

    private static StringBuilder sql = null;

    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(MySQL.URL.value, MySQL.USER.value, MySQL.PASS.value)) {

            System.out.println(
                    "This program displays the Director, Genre and Rating of movies with the name supplied by a user.");
            String movieTitle = getMovieInput();

            ResultSet movieResultSet = getMovieResultSet(connection, movieTitle);

            if (movieResultSet == null) {
                System.out.println("Oops! Something went wrong.");
                System.exit(-1);
            }

            String tableHeaders = createRow("Movie Title", "Director/Actors", "Genre", "Rating");
            String tableLines = createRow("-----------", "---------------", "-----", "------");
            System.out.print("\n" + tableHeaders + tableLines);

            List<Movie> movies = getMovieList(connection, movieResultSet);

            for (Movie movie : movies) {
                System.out.print(createRow(movie.getMovieTitle(), movie.getDirector().getFullName(), movie.getGenre(),
                        movie.getRating()));
                for (Actor actor : movie.getActors()) {
                    System.out.print(createRow("", actor.getFullName(), "", ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createRow(String title, String person, String genre, String rating) {
        return String.format("%-25s%-25s%-20s%-10s%n", title, person, genre, rating);
    }

    private static String getMovieInput() {
        Scanner input = new Scanner(System.in);
        System.out.print("\nPlease enter a movie title: ");
        String movieTitle = input.nextLine();
        input.close();
        return movieTitle;
    }

    private static ResultSet getMovieResultSet(Connection connection, String movieTitle) {
        sql = new StringBuilder();
        sql.append("SELECT m.movie_name, m.movie_length, m.release_date, ");
        sql.append("d.first_name, d.last_name, r.rating, g.genre, m.movie_id ");
        sql.append("FROM movies m ");
        sql.append("JOIN directors d ON d.director_ID = m.director_id ");
        sql.append("JOIN ratings r ON r.rating_id = m.rating_id ");
        sql.append("JOIN genres g ON g.genre_id = m.genre_id ");
        sql.append("WHERE m.movie_name = ?;");

        try {
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            statement.setString(1, movieTitle);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("\nSQL (copy the SQL statement below and run it if you're having problems): \n"
                    + sql.toString() + "\n");
        }
        return null;
    }

    private static List<Movie> getMovieList(Connection connection, ResultSet movieResultSet) {
        List<Movie> movies = new ArrayList<Movie>();

        try {

            while (movieResultSet.next()) {
                Movie movie = new Movie();
                movie.setMovieTitle(movieResultSet.getString("movie_name"));
                movie.setMovieLength(movieResultSet.getInt("movie_length"));
                movie.setReleaseDate(movieResultSet.getDate("release_date"));
                movie.setGenre(movieResultSet.getString("genre"));
                movie.setRating(movieResultSet.getString("rating"));
                movie.setId(movieResultSet.getInt("movie_id"));

                // Since movie has a Director object as a member variable, lets create one
                Director director = new Director();
                director.setFirstName(movieResultSet.getString("first_name"));
                director.setLastName(movieResultSet.getString("last_name"));

                // Now add the director object to the movie object
                movie.setDirector(director);

                // Get the actors as an ArrayList of Actor objects
                movie.setActors(getActorList(connection, movie));
                
                movies.add(movie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("\nSQL (copy the SQL statement below and run it if you're having problems): \n"
                    + sql.toString() + "\n");
        }
        return movies;
    }

    private static List<Actor> getActorList(Connection connection, Movie movie) {
        ArrayList<Actor> actors = new ArrayList<Actor>();
        sql = new StringBuilder();
        sql.append("SELECT m.movie_name, a.first_name, a.last_name ");
        sql.append("FROM movies m ");
        sql.append("JOIN movies_actors ma ON ma.movie_id = m.movie_id ");
        sql.append("JOIN actors a ON a.actor_id = ma.actor_id ");
        sql.append("WHERE m.movie_id = ?;");
        // Note the Movie ID, not Movie Title, is used to select the actors.
        // This is so that if multiple different movies exist with the same name,
        // Each won't have the same list of actors. The result is true to the
        // movies_actors relationship table.

        try {
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            statement.setInt(1, movie.getId());
            ResultSet actorResultSet = statement.executeQuery();

            while (actorResultSet.next()) {
                Actor actor = new Actor();
                actor.setFirstName(actorResultSet.getString("first_name"));
                actor.setLastName(actorResultSet.getString("last_name"));
                actors.add(actor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("\nSQL (copy the SQL statement below and run it if you're having problems): \n"
                    + sql.toString() + "\n");
        }

        return actors;
    }
}
