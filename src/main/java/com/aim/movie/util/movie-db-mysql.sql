-- CREATE DATABASE movie;

USE movie;

DROP TABLE IF EXISTS movies_actors;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS directors;
DROP TABLE IF EXISTS actors;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS genres;

CREATE TABLE directors (
	director_id INT AUTO_INCREMENT PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	date_of_birth DATE
);

CREATE TABLE ratings (
	rating_id INT AUTO_INCREMENT PRIMARY KEY,
	rating VARCHAR(5) NOT NULL,
	description VARCHAR(50)
);

insert into ratings (rating, description) values ('G', 'General Audiences');
insert into ratings (rating, description) values ('PG', 'Parental Guidance Suggested');
insert into ratings (rating, description) values ('PG-13', 'Parents Strongly Cautioned');
insert into ratings (rating, description) values ('R', 'Restricted');

CREATE TABLE genres (
	genre_id INT AUTO_INCREMENT PRIMARY KEY,
	genre VARCHAR(25) NOT NULL
);

insert into genres (genre) values ('Action');
insert into genres (genre) values ('Animation');
insert into genres (genre) values ('Comedy');
insert into genres (genre) values ('Sci-Fi');
insert into genres (genre) values ('Drama, Sci-Fi');


TRUNCATE TABLE directors;
insert into directors (first_name, last_name, date_of_birth) values ('James', 'Cameron', '1954-08-16');
insert into directors (first_name, last_name, date_of_birth) values ('J.J.', 'Abrams', '1966-06-27');
insert into directors (first_name, last_name, date_of_birth) values ('Steven', 'Speilberg', '1946-12-18');

CREATE TABLE actors (
	actor_id INT AUTO_INCREMENT PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	date_of_birth DATE
);
TRUNCATE TABLE actors;
insert into actors (first_name, last_name, date_of_birth) values ('Sam', 'Worthington', '1976-08-02');
insert into actors (first_name, last_name, date_of_birth) values ('Zoe', 'Saldana', '1978-06-19');
insert into actors (first_name, last_name, date_of_birth) values ('Sigourney', 'Weaver', '1949-10-08');
insert into actors (first_name, last_name, date_of_birth) values ('John', 'Cho', '1972-06-16');
insert into actors (first_name, last_name, date_of_birth) values ('Chris', 'Pine', '1980-08-26');
insert into actors (first_name, last_name, date_of_birth) values ('Richard', 'Dreyfuss', '1947-10-29');

CREATE TABLE movies (
	movie_id INT AUTO_INCREMENT PRIMARY KEY,
	director_id INT,
    genre_id INT,
    rating_id INT NOT NULL,
	movie_name VARCHAR(50) NOT NULL,
	movie_length INT,
	release_date DATE, 
	FOREIGN KEY (genre_id) REFERENCES genres(genre_id),
	FOREIGN KEY (director_id) REFERENCES directors(director_id)
);

INSERT INTO movies (movie_name, movie_length, release_date, director_id, rating_id, genre_id) values ('Avatar', 162, '2009-12-18', 1, 3, 4);
INSERT INTO movies (movie_name, movie_length, release_date, director_id, rating_id, genre_id) values ('Star Trek', 127, '2009-05-08', 2, 3, 5);
INSERT INTO movies (movie_name, movie_length, release_date, director_id, rating_id, genre_id) values ('Close Encounters of the Third Kind', 135, '1977-11-16', 3, 2, 4);

CREATE TABLE movies_actors (
	id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT,
    actor_id INT,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES actors(actor_id)
);

INSERT INTO movies_actors (movie_id, actor_id) VALUES (1,1);
INSERT INTO movies_actors (movie_id, actor_id) VALUES (1,2);
INSERT INTO movies_actors (movie_id, actor_id) VALUES (1,3);
INSERT INTO movies_actors (movie_id, actor_id) VALUES (2,4);
INSERT INTO movies_actors (movie_id, actor_id) VALUES (2,5);
INSERT INTO movies_actors (movie_id, actor_id) VALUES (3,6);

-- Misc queries:

-- DELETE FROM movies WHERE movie_id = 4;
-- select * from actors where first_name = 'Sam' and last_name = 'Worthington' ;
-- select * from ratings;
-- select * from actors;
-- select * from directors;
-- select * from movies;
-- select count(*) from movies;
-- select * from movies_actors;
-- select m.movie_id, m.movie_name, a.first_name, a.last_name FROM movies m JOIN movies_actors ma ON ma.movie_id = m.movie_id JOIN actors a ON a.actor_id = ma.actor_id WHERE m.movie_name = 'Avatar';
-- select * from movies;
-- select * from movies where movie_name = 'Avatar';
-- SELECT movie_name, first_name, last_name FROM movies m JOIN directors d ON d.director_id = m.director_id WHERE m.movie_name = 'Avatar';
-- select * from genres;
-- select m.movie_name, m.movie_length, m.release_date, d.first_name, d.last_name, r.rating, g.genre from movies m join directors d on d.director_id = m.director_id join ratings r on r.rating_id = m.rating_id join genres g on g.genre_id = m.genre_id where m.movie_name = 'Star Trek';