package com.aim.movie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class App {
    public static final String URL = "jdbc:mysql://localhost:3306/movie1";
    private static final String USER = "root";
    private static final String PASS = "Root";

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from movies");

            while (resultSet.next()) {
                String movieTitle = resultSet.getString("movie_name");
                System.out.println("Movie Title: " + movieTitle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(String.valueOf(true)); // Outputs "true"
    }

}
