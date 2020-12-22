package com.aim.movie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.aim.movie.domain.Director;
import com.aim.movie.domain.Movie;
import com.aim.movie.util.MySQL;

public class Exercise1 {

    private static StringBuilder sql = null;

    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(MySQL.URL.value, MySQL.USER.value, MySQL.PASS.value)) {

            System.out.println(
                    "This program displays the Director, Genre and Rating of movies with the name supplied by a user.");
            String movieTitle = getMovieInput();

            ResultSet resultSet = getMovieResultSet(connection, movieTitle);
            
            if (resultSet == null) {
                System.out.println("Oops! Something went wrong.");
                System.exit(-1);
            }

            String tableHeaders = createRow("Movie Title", "Director", "Genre", "Rating");
            String tableLines = createRow("-----------", "--------", "-----", "------");
            System.out.print("\n" + tableHeaders + tableLines);

            List<Movie> movies = getMovies(resultSet);

            for (Movie movie : movies) {
                System.out.print(createRow(movie.getMovieTitle(), movie.getDirector().getFullName(), movie.getGenre(),
                        movie.getRating()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createRow(String title, String person, String genre, String rating) {
        return String.format("%-25s%-20s%-18s%-10s%n", title, person, genre, rating);
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
        sql.append("d.first_name, d.last_name, r.rating, g.genre ");
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

    private static List<Movie> getMovies(ResultSet resultSet) {
        List<Movie> movies = new ArrayList<Movie>();

        try {
            while (resultSet.next()) {
                Movie movie = new Movie();
                movie.setMovieTitle(resultSet.getString("movie_name"));
                movie.setMovieLength(resultSet.getInt("movie_length"));
                movie.setReleaseDate(resultSet.getDate("release_date"));
                movie.setGenre(resultSet.getString("genre"));
                movie.setRating(resultSet.getString("rating"));

                // Since movie has a Director object as a member variable, lets create one
                Director director = new Director();
                director.setFirstName(resultSet.getString("first_name"));
                director.setLastName(resultSet.getString("last_name"));

                // Now add the director object to the movie object
                movie.setDirector(director);
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("\nSQL (copy the SQL statement below and run it if you're having problems): \n"
                    + sql.toString() + "\n");
        }
        return movies;
    }
}
