package com.aim.movie.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.aim.movie.domain.Actor;
import com.aim.movie.domain.Director;
import com.aim.movie.domain.Movie;

public class MovieDBUtils {

    private static StringBuilder sql = null;

    // Convert a queried ResultSet of movie information into a list of Movie objects
    public static List<Movie> getMovieList(Connection connection, String movieTitle) {
        // First get the ResultSet based on the user's input
        ResultSet movieResultSet = getMovieResultSet(connection, movieTitle);

        List<Movie> movies = new ArrayList<Movie>();

        try {

            while (movieResultSet.next()) {
                // For each row in the ResultSet, create a Movie object and add
                // it to the movies list
                Movie movie = new Movie();
                movie.setMovieTitle(movieResultSet.getString("movie_name"));
                movie.setMovieLength(movieResultSet.getInt("movie_length"));
                movie.setReleaseDate(movieResultSet.getDate("release_date"));
                movie.setGenre(movieResultSet.getString("genre"));
                movie.setRating(movieResultSet.getString("rating"));
                movie.setId(movieResultSet.getInt("movie_id"));
                Director director = new Director();
                director.setFirstName(movieResultSet.getString("first_name"));
                director.setLastName(movieResultSet.getString("last_name"));
                movie.setDirector(director);

                // Get the actors as an ArrayList of Actor objects.
                // Requires connection to query the actors table via the
                // movies_actors many-to-many map table.
                movie.setActors(getActorList(connection, movie));

                movies.add(movie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    // generate a ResultSet of movie information based on a user's input. Can be
    // a single row or multiple rows with movies starting with the user's input.
    private static ResultSet getMovieResultSet(Connection connection, String movieTitle) {
        sql = new StringBuilder();
        sql.append("SELECT m.movie_name, m.movie_length, m.release_date, ");
        sql.append("d.first_name, d.last_name, r.rating, g.genre, m.movie_id ");
        sql.append("FROM movies m ");
        sql.append("JOIN directors d ON d.director_ID = m.director_id ");
        sql.append("JOIN ratings r ON r.rating_id = m.rating_id ");
        sql.append("JOIN genres g ON g.genre_id = m.genre_id ");
        sql.append("WHERE m.movie_name LIKE ?;");

        try {
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            statement.setString(1, movieTitle + "%");
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Return a list of Actor objects for a given movie object based on the
    // movie ID and the movies_actors many-to-many relationship table
    private static List<Actor> getActorList(Connection connection, Movie movie) {
        ArrayList<Actor> actors = new ArrayList<Actor>();
        try {
            sql = new StringBuilder();
            sql.append("SELECT m.movie_name, a.first_name, a.last_name ");
            sql.append("FROM movies m ");
            sql.append("JOIN movies_actors ma ON ma.movie_id = m.movie_id ");
            sql.append("JOIN actors a ON a.actor_id = ma.actor_id ");
            sql.append("WHERE m.movie_id = ?;");

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
        }
        return actors;
    }

    // insert a movie object into the database
    public static void insertMovie(Connection connection, Movie movie) {
        String movieName = movie.getMovieTitle();
        int movieLength = movie.getMovieLength();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(movie.getReleaseDate());

        // update the Directors table and get the director ID
        String firstName = movie.getDirector().getFirstName();
        String lastName = movie.getDirector().getLastName();
        addStringEntries(connection, "directors", new String[] { "first_name", "last_name" },
                new String[] { firstName, lastName });
        int directorId = getIntFromTable(connection, "directors", "director_id",
                new String[] { "first_name", "last_name" }, new String[] { firstName, lastName });

        // update the Ratings table and get the rating ID
        addStringEntries(connection, "ratings", new String[] { "rating" }, new String[] { movie.getRating() });
        int ratingId = getIntFromTable(connection, "ratings", "rating_id", new String[] { "rating" },
                new String[] { movie.getRating() });

        // update the Genres table and get the genre ID
        addStringEntries(connection, "genres", new String[] { "genre" }, new String[] { movie.getGenre() });
        int genreId = getIntFromTable(connection, "genres", "genre_id", new String[] { "genre" },
                new String[] { movie.getGenre() });

        // update the movies table with the new movie information
        try {
            // can't use the addStringEntries method because some values are int
            sql = new StringBuilder();
            sql.append("INSERT INTO movies (movie_name, movie_length, ");
            sql.append("release_date, director_id, rating_id, genre_id) ");
            sql.append("values (?, ?, ?, ?, ?, ?)");

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            statement.setString(1, movieName);
            statement.setInt(2, movieLength);
            statement.setString(3, date);
            statement.setInt(4, directorId);
            statement.setInt(5, ratingId);
            statement.setInt(6, genreId);
            statement.executeUpdate();

            // update the movie_actors map table so that actors can be listed
            updateActors(connection, movie);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addStringEntries(Connection connection, String table, String[] columns, String[] values) {
        // This method adds VALUES to COLUMNS in TABLE, but only if the data
        // being entered into non-key fields is unique.
        try {
            List<String> c = new ArrayList<>(List.of(columns));
            List<String> v = new ArrayList<>(List.of(values));
            String string1 = "(" + String.join(", ", columns) + ")";
            String string2 = String.join(", ", v.stream().map(s -> "?").collect(Collectors.toList()));
            String string3 = String.join(" AND ", c.stream().map(s -> s + " = ?").collect(Collectors.toList()));

            sql = new StringBuilder();
            sql.append("INSERT INTO " + table + " " + string1);
            sql.append(" SELECT " + string2 + " FROM DUAL ");
            sql.append("WHERE NOT EXISTS(SELECT * FROM " + table);
            sql.append(" WHERE " + string3 + " LIMIT 1);");

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            for (int i = 1; i <= v.size(); i++) {
                statement.setString(i, v.get(i - 1));
                statement.setString(i + v.size(), v.get(i - 1));
            }
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getIntFromTable(Connection connection, String table, String selectionColumn,
            String[] conditionalColumns, String[] conditionalValues) {
        try {
            List<String> columns = new ArrayList<>(List.of(conditionalColumns));
            List<String> values = new ArrayList<>(List.of(conditionalValues));
            String string = String.join(" AND ", columns.stream().map(s -> s + " = ?").collect(Collectors.toList()));
            sql = new StringBuilder();
            sql.append("SELECT " + selectionColumn + " FROM " + table);
            sql.append(" WHERE " + string + ";");
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            for (int i = 1; i <= values.size(); i++) {
                statement.setString(i, values.get(i - 1));
            }
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(selectionColumn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // update the movies_actors relationship table using a movie object
    private static void updateActors(Connection connection, Movie movie) {
        List<Actor> actors = movie.getActors();
        try {
            // get movie ID of movie being used to update movies_actors
            int movieId = getIntFromTable(connection, "movies", "movie_id", new String[] { "movie_name" },
                    new String[] { movie.getMovieTitle() });

            for (Actor actor : actors) {
                // add actor to actors table
                String firstName = actor.getFirstName();
                String lastName = actor.getLastName();
                addStringEntries(connection, "actors", new String[] { "first_name", "last_name" },
                        new String[] { firstName, lastName });

                // get actor id
                int actorId = getIntFromTable(connection, "actors", "actor_id",
                        new String[] { "first_name", "last_name" }, new String[] { firstName, lastName });

                // update the movies_actors table
                Statement s = connection.createStatement();
                s.executeUpdate(
                        "INSERT INTO movies_actors (movie_id, actor_ID) VALUES (" + movieId + "," + actorId + ");");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMovie(Connection connection, Movie movie) {
        try {
            sql = new StringBuilder();
            sql.append("DELETE FROM movies WHERE movie_id = ?;");
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            statement.setInt(1, movie.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
