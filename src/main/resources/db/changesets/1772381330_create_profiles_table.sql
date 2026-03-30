CREATE TYPE skill_level AS ENUM ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'PROFESSIONAL');

CREATE TABLE profiles
(
    id                  UUID PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    location            VARCHAR(255),
    skill               VARCHAR(255),
    years_of_experience INTEGER CHECK (years_of_experience >= 0),
    description         TEXT,
--                           created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    date_of_birth       TIMESTAMP
);

CREATE TABLE instruments
(
    id   UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE genres
(
    id   UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE profile_instruments
(
    profile_id    UUID REFERENCES profiles (id) ON DELETE CASCADE,
    instrument_id UUID REFERENCES instruments (id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, instrument_id)
);

CREATE TABLE profile_genres
(
    profile_id UUID REFERENCES profiles (id) ON DELETE CASCADE,
    genre_id   UUID REFERENCES genres (id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, genre_id)
);

CREATE TABLE media_metadata
(
    id         UUID PRIMARY KEY,
    profile_id UUID REFERENCES profiles (id) ON DELETE CASCADE,
    type       VARCHAR(20),
    url        TEXT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE
--     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE profile_links
(
    id         UUID PRIMARY KEY,
    profile_id UUID REFERENCES profiles (id) ON DELETE CASCADE,
    link_type  VARCHAR(50),
    url        TEXT NOT NULL,
    title      VARCHAR(100)
--     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE search_filters
(
    id              UUID PRIMARY KEY,
    profile_id      UUID REFERENCES profiles (id) ON DELETE CASCADE,
    name            VARCHAR(255),
    target_location VARCHAR(255),
    target_skill    skill_level,
    min_experience  INTEGER,
    is_active       BOOLEAN                  DEFAULT TRUE
--     created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE filter_instruments
(
    filter_id     UUID REFERENCES search_filters (id) ON DELETE CASCADE,
    instrument_id UUID REFERENCES instruments (id) ON DELETE CASCADE,
    PRIMARY KEY (filter_id, instrument_id)
);

CREATE TABLE filter_genres
(
    filter_id UUID REFERENCES search_filters (id) ON DELETE CASCADE,
    genre_id  UUID REFERENCES genres (id) ON DELETE CASCADE,
    PRIMARY KEY (filter_id, genre_id)
);

INSERT INTO instruments(id, name) VALUES ('10f76c61-4dcd-487d-ad19-866bcaace579', 'GUITAR');
INSERT INTO instruments(id, name) VALUES ('10f76c61-4dcd-487d-ad19-866bcaace580', 'DRUMS');

INSERT INTO genres(id, name) VALUES ('10f76c61-4dcd-487d-ad19-866bcaace579', 'METAL');
INSERT INTO genres(id, name) VALUES ('10f76c61-4dcd-487d-ad19-866bcaace580', 'ROCK');