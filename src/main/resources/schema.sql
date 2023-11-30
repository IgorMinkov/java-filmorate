DROP TABLE IF EXISTS ikes CASCADE;
DROP TABLE IF EXISTS friendship CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS mpa_rating CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS review_rating CASCADE;


CREATE TABLE IF NOT EXISTS users (
    user_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email     VARCHAR(127) UNIQUE NOT NULL,
    login     VARCHAR(127) UNIQUE NOT NULL,
    name      VARCHAR(127),
    birthday  DATE CHECK (birthday < CURRENT_DATE)
    );

CREATE TABLE IF NOT EXISTS mpa_rating (
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    rating_name  VARCHAR(63) NOT NULL
    );

CREATE TABLE IF NOT EXISTS films (
    film_id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(200) NOT NULL,
    release_date  DATE NOT NULL,
    duration      BIGINT NOT NULL CHECK (duration > 0),
    mpa_rating    INTEGER REFERENCES mpa_rating(id) ON DELETE NO ACTION
    );

CREATE TABLE IF NOT EXISTS genres (
    id     INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre  VARCHAR(63) NOT NULL
    );

CREATE TABLE IF NOT EXISTS film_genres (
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id   BIGINT REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id  BIGINT REFERENCES genres (id)
    );

CREATE TABLE IF NOT EXISTS likes (
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id    BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    film_id    BIGINT REFERENCES films (film_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS friendship (
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id    BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id  BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    approved   BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS reviews (
    review_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id     BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    film_id     BIGINT REFERENCES films (film_id) ON DELETE CASCADE,
    content     VARCHAR(255) NOT NULL,
    is_positive BIT NOT NULL,
    useful      INTEGER DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS review_rating (
    review_id   BIGINT REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id     BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    is_positive BIT NOT NULL,
    PRIMARY KEY (review_id, user_id)
    );
