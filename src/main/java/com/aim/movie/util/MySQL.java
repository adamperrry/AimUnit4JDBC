package com.aim.movie.util;

public enum MySQL {
    URL("jdbc:mysql://localhost:3306/movie?useUnicode=true&useJDBCCompliant"
            + "TimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"),
    USER("root"), 
    PASS("Root");

    public final String value;

    private MySQL(String value) {
        this.value = value;
    }
}
