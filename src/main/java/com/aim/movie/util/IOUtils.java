package com.aim.movie.util;

import java.util.List;

import com.aim.movie.domain.Actor;
import com.aim.movie.domain.Movie;

import org.apache.commons.lang3.StringUtils;

public class IOUtils {
    
    private static Input input = Input.getInstance();

    // get a user's search input to be checked against movie titles
    public static String getMovieInput() {
        String movieTitle = input.getStringOrDefault("\nPlease enter a movie title (blank to list all movies): ", "");
        return movieTitle;
    }

    // Print a list of movies (and their actors)
    public static void printMovies(List<Movie> movies) {
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
    public static String createRow(String title, String person, String genre, String rating) {
        return String.format("%-27s%-25s%-18s%-10s%n", title, person, genre, rating);
    }

    // check to see if movie should be added to database
    public static Boolean shouldAddMovie() {
        char answer = input.getChar("\nThis movie does not exist. Would you like to add it (y/n)? ");
        answer = Character.toUpperCase(answer);
        return (answer == 'Y') ? true : false;
    }

    public static String updateOrDelete() {
        String prompt = "\nWould you like to update or delete the top result (u/d/n)? ";
        char answer = Character.toUpperCase(input.getChar(prompt));
        if (answer == 'U') {
            return "update";
        } else if (answer == 'D') {
            return "delete";
        }
        return "no";
    }
}
