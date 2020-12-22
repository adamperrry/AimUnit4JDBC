package com.aim.movie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.aim.movie.domain.Actor;
import com.aim.movie.domain.Director;
import com.aim.movie.domain.Movie;
import com.aim.movie.util.MySQL;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;

public class Exercise5 {
    private static StringBuilder sql = null;
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(MySQL.URL.value, MySQL.USER.value, MySQL.PASS.value)) {

            System.out.println(
                    "This program displays the Director, Genre and Rating of movies with the name supplied by a user.");

            String movieTitle = getMovieInput();
            List<Movie> movies = getMovieList(connection, movieTitle);
            if (movies.size() > 0) {
                printMovies(movies);
            } else if (shouldAddMovie()) {
                Movie newMovie = createMovieFromUserInput();
                insertMovie(connection, newMovie);
                movies = getMovieList(connection, newMovie.getMovieTitle());
                printMovies(movies);
            }

            Movie movie = movies.get(0);
            String choice = updateOrDelete();
            if (choice == "update") {
                updateMovie(movie);
                deleteMovie(connection, movie);
                insertMovie(connection, movie);
                System.out.println(movie.getMovieTitle() + " has been updated.");
            } else if (choice == "delete") {
                deleteMovie(connection, movie);
                System.out.println(movie.getMovieTitle() + " has been deleted.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        input.close();
        System.out.println("\nGoodbye...");
    }

    // get a user's search input to be checked against movie titles
    private static String getMovieInput() {
        System.out.print("\nPlease enter a movie title: ");
        String movieTitle = input.nextLine();
        return movieTitle;
    }

    // Convert a queried ResultSet of movie information into a list of Movie objects
    private static List<Movie> getMovieList(Connection connection, String movieTitle) {
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

    // Print a list of movies (and their actors)
    private static void printMovies(List<Movie> movies) {
        String tableHeaders = createRow("Movie Title", "Director/Actors", "Genre", "Rating");
        String tableLines = createRow("-----------", "---------------", "-----", "------");
        System.out.print("\n" + tableHeaders + tableLines);

        for (Movie movie : movies) {
            System.out.print(createRow(StringUtils.abbreviate(movie.getMovieTitle(), 25),
                    movie.getDirector().getFullName(), movie.getGenre(), movie.getRating()));
            for (Actor actor : movie.getActors()) {
                System.out.print(createRow("", actor.getFullName(), "", ""));
            }
        }
    }

    // Create a formatted row for the output table
    private static String createRow(String title, String person, String genre, String rating) {
        return String.format("%-27s%-25s%-18s%-10s%n", title, person, genre, rating);
    }

    // check to see if movie should be added to database
    private static Boolean shouldAddMovie() {
        System.out.print("\nThis movie does not exist. Would you like to add it (y/n)? ");
        char answer = Character.toUpperCase(input.next().charAt(0));
        return (answer == 'Y') ? true : false;
    }

    // create a movie from use input
    private static Movie createMovieFromUserInput() {
        Movie newMovie = new Movie();
        // using GenericValidator, so must be in a try block
        try {
            // consider adding data validation?
            System.out.println("\nPlease enter the information for the movie.");
            System.out.print("Movie title: ");
            input.nextLine();
            newMovie.setMovieTitle(input.nextLine());

            System.out.print("Movie length (min): ");
            String length = input.nextLine();
            while (!GenericValidator.isInt(length)) {
                System.out.print("Oops. Enter a valid movie length (min): ");
                length = input.nextLine();
            }
            newMovie.setMovieLength(Integer.parseInt(length));

            System.out.print("Release Date (yyyy-mm-dd): ");
            String dateString = input.nextLine();
            while (!GenericValidator.isDate(dateString, "yyyy-MM-dd", false)) {
                System.out.print("Oops. Enter a valid Release Date (yyyy-MM-dd): ");
                dateString = input.nextLine();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            newMovie.setReleaseDate(formatter.parse(dateString));

            System.out.print("Genre: ");
            newMovie.setGenre(input.nextLine());

            System.out.print("Rating (G, PG, PG-13, R): ");
            newMovie.setRating(input.nextLine().toUpperCase());

            System.out.print("Director: ");
            Director director = new Director();
            director.setFullName(input.nextLine());
            newMovie.setDirector(director);

            System.out.print("Actor: ");
            Actor actor = new Actor();
            actor.setFullName(input.nextLine());
            List<Actor> actors = new ArrayList<>(List.of(actor));
            newMovie.setActors(actors);

            System.out.print("\nThanks for adding a movie to the database!\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newMovie;
    }

    // insert a movie object into the database
    private static void insertMovie(Connection connection, Movie movie) {
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

    private static String updateOrDelete() {
        System.out.print("\nWould you like to update or delete the top result (u/d)? ");
        char answer = Character.toUpperCase(input.next().charAt(0));
        if (answer == 'U') {
            return "update";
        } else if (answer == 'D') {
            return "delete";
        }
        return "no";
    }

    private static void updateMovie(Movie movie) {
        try {
            input.nextLine();
            System.out.print("Change movie title (" + movie.getMovieTitle() + "):");
            String newMovieTitle = input.nextLine();
            if (!GenericValidator.isBlankOrNull(newMovieTitle)) {
                movie.setMovieTitle(newMovieTitle);
            }

            System.out.print("Change movie length (min) (" + movie.getMovieLength() + "):");
            String newMovieLength = input.nextLine();
            while (!GenericValidator.isInt(newMovieLength) && !GenericValidator.isBlankOrNull(newMovieLength)) {
                System.out.print("Oops. Enter a valid movie length (min): ");
                newMovieLength = input.nextLine();
            }
            if (!GenericValidator.isBlankOrNull(newMovieLength)) {
                movie.setMovieLength(Integer.parseInt(newMovieLength));
            }

            System.out.print("Change movie release date (yyyy-mm-dd) (" + movie.getReleaseDate().toString() + "):");
            String newReleaseDate = input.nextLine();
            while (!GenericValidator.isInt(newReleaseDate) && !GenericValidator.isBlankOrNull(newReleaseDate)) {
                System.out.print("Oops. Enter a valid Release Date (yyyy-MM-dd): ");
                newReleaseDate = input.nextLine();
            }
            if (!GenericValidator.isBlankOrNull(newReleaseDate)) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                movie.setReleaseDate(formatter.parse(newReleaseDate));
            }

            System.out.print("Change movie genre (" + movie.getGenre() + "):");
            String newMovieGenre = input.nextLine();
            if (!GenericValidator.isBlankOrNull(newMovieGenre)) {
                movie.setMovieTitle(newMovieGenre);
            }

            System.out.print("Change movie rating (" + movie.getRating() + "):");
            String newMovieRating = input.nextLine();
            if (!GenericValidator.isBlankOrNull(newMovieRating)) {
                movie.setMovieTitle(newMovieRating);
            }

            System.out.print("Change movie director (" + movie.getDirector().getFullName() + "):");
            String newMovieDirector = input.nextLine();
            if (!GenericValidator.isBlankOrNull(newMovieDirector)) {
                Director director = new Director();
                director.setFullName(newMovieDirector);
                movie.setDirector(director);
            }

            System.out.print("Change movie actor (" + movie.getActors().get(0).getFullName() + "):");
            String newMovieActor = input.nextLine();
            if (!GenericValidator.isBlankOrNull(newMovieActor)) {
                Actor actor = new Actor();
                actor.setFullName(newMovieActor);
                List<Actor> actors = new ArrayList<>(List.of(actor));
                movie.setActors(actors);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteMovie(Connection connection, Movie movie) {
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
