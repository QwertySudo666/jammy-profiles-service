-- 1774911908_insert_test_data.sql
--liquibase formatted sql

--changeset author:me id:1 context:dev
INSERT INTO profiles (id, user_id, name, location, skill, years_of_experience, description, date_of_birth) VALUES
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'James Hetfield', 'Los Angeles, CA', 'PROFESSIONAL', 40, 'Rhythm guitar and vocals for Metallica. Master of downpicking.', '1963-08-03'),
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'Lars Ulrich', 'San Francisco, CA', 'PROFESSIONAL', 42, 'Metallica co-founder. Drummer known for double-bass style.', '1963-12-26'),
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'Tony Iommi', 'Birmingham, UK', 'PROFESSIONAL', 55, 'The Godfather of Metal. Black Sabbath lead guitarist.', '1948-02-19'),
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'Dave Grohl', 'Warren, OH', 'PROFESSIONAL', 35, 'Nirvana drummer and Foo Fighters frontman. Rock enthusiast.', '1969-01-14'),
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'Slash', 'Hampstead, London', 'PROFESSIONAL', 40, 'Guns N Roses lead guitarist. Known for iconic riffs and solos.', '1965-07-23'),
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'Danny Carey', 'Lawrence, KS', 'PROFESSIONAL', 38, 'Tool drummer. Master of polyrhythms and geometric patterns.', '1961-05-10'),
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'Zakk Wylde', 'Bayonne, NJ', 'PROFESSIONAL', 35, 'Ozzy Osbourne guitarist and Black Label Society founder.', '1967-01-14'),
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'Mario Duplantier', 'Bayonne, France', 'PROFESSIONAL', 25, 'Gojira drummer. Known for precision and technical death metal.', '1981-06-19'),
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'Nita Strauss', 'Los Angeles, CA', 'ADVANCED', 15, 'Solo artist and Alice Cooper guitarist. Hurricane of shred.', '1986-12-07'),
                                                                                                      (gen_random_uuid(), gen_random_uuid(), 'Eloy Casagrande', 'Santo Andre, Brazil', 'PROFESSIONAL', 20, 'Powerhouse drummer, currently with Slipknot.', '1991-01-29');

--changeset author:me id:2 context:dev
INSERT INTO profile_instruments (profile_id, instrument_id)
SELECT id, '10f76c61-4dcd-487d-ad19-866bcaace579'::uuid FROM profiles
WHERE name IN ('James Hetfield', 'Tony Iommi', 'Slash', 'Zakk Wylde', 'Nita Strauss');

--changeset author:me id:3 context:dev
INSERT INTO profile_instruments (profile_id, instrument_id)
SELECT id, '10f76c61-4dcd-487d-ad19-866bcaace580'::uuid FROM profiles
WHERE name IN ('Lars Ulrich', 'Dave Grohl', 'Danny Carey', 'Mario Duplantier', 'Eloy Casagrande');

--changeset author:me id:4 context:dev
INSERT INTO profile_genres (profile_id, genre_id)
SELECT id, '10f76c61-4dcd-487d-ad19-866bcaace579'::uuid FROM profiles
WHERE name IN ('James Hetfield', 'Lars Ulrich', 'Tony Iommi', 'Zakk Wylde', 'Mario Duplantier', 'Eloy Casagrande');

--changeset author:me id:5 context:dev
INSERT INTO profile_genres (profile_id, genre_id)
SELECT id, '10f76c61-4dcd-487d-ad19-866bcaace580'::uuid FROM profiles
WHERE name IN ('Dave Grohl', 'Slash', 'Danny Carey', 'Nita Strauss');
