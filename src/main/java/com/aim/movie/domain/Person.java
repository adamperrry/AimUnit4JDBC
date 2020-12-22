package com.aim.movie.domain;

import java.util.Date;

public class Person {
    private int id;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setFullName(String fullName) {
        String[] name = fullName.split(" ");
        if (name != null && name.length >= 2) {
            firstName = name[0];
            // skip middle names and just get last
            lastName = name[name.length - 1];
        } else {
            firstName = fullName;
        }
    }

    public String toString() {
        return "Id: " + id + ", FirstName: " + firstName + ", LastName: " + lastName;
    }

}