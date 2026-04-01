package jammy.platform.resources;

import static io.restassured.RestAssured.given;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import jammy.platform.entities.GenreEntity;
import jammy.platform.entities.InstrumentEntity;
import jammy.platform.entities.ProfileEntity;
import jammy.platform.enums.SkillLevel;
import jammy.platform.models.SearchFilter;
import jammy.platform.repositories.GenreRepository;
import jammy.platform.repositories.InstrumentRepository;
import jammy.platform.repositories.ProfileRepository;
import jammy.platform.requests.ProfileCreateRequest;
import jammy.platform.requests.ProfileUpdateRequest;
import jammy.platform.responses.PagedResponse;
import jammy.platform.responses.ProfileResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@AllArgsConstructor
public class ProfileControllerTest {

  private final ProfileRepository profileRepository;
  private final InstrumentRepository instrumentRepository;
  private final GenreRepository genreRepository;

  private final Set<UUID> createdProfileIds = new HashSet<>();
  private final Set<UUID> createdInstrumentIds = new HashSet<>();
  private final Set<UUID> createdGenreIds = new HashSet<>();

  private final String basicPath = "/profiles";

  @AfterEach
  void cleanup() {
    QuarkusTransaction.requiringNew()
        .run(
            () -> {
              createdProfileIds.forEach(profileRepository::delete);
              createdInstrumentIds.forEach(instrumentRepository::delete);
              createdGenreIds.forEach(genreRepository::delete);
            });
    createdProfileIds.clear();
    createdInstrumentIds.clear();
    createdGenreIds.clear();
  }

  ProfileEntity persistRandomProfile(String name) {
    return QuarkusTransaction.requiringNew()
        .call(
            () -> {
              ProfileEntity entity =
                  ProfileEntity.builder()
                      .id(UUID.randomUUID())
                      .name(name)
                      .location("Lviv")
                      .skill(SkillLevel.INTERMEDIATE)
                      .dateOfBirth(Instant.parse("2000-01-01T00:00:00Z"))
                      .instruments(new HashSet<>())
                      .genres(new HashSet<>())
                      .build();
              profileRepository.create(entity);
              createdProfileIds.add(entity.getId());
              return entity;
            });
  }

  InstrumentEntity persistInstrument(String name) {
    return QuarkusTransaction.requiringNew()
        .call(
            () -> {
              InstrumentEntity entity = instrumentRepository.create(name);
              createdInstrumentIds.add(entity.getId());
              return entity;
            });
  }

  GenreEntity persistGenre(String name) {
    return QuarkusTransaction.requiringNew()
        .call(
            () -> {
              GenreEntity entity = genreRepository.create(name);
              createdGenreIds.add(entity.getId());
              return entity;
            });
  }

  ProfileCreateRequest generateProfileCreateRequest() {
    return new ProfileCreateRequest(
        "Matt Tuck",
        "London",
        SkillLevel.INTERMEDIATE,
        2,
        "Description",
        OffsetDateTime.parse("2000-01-01T00:00:00Z"),
        new HashSet<>(Set.of("GUITAR")),
        new HashSet<>(Set.of("METAL")));
  }

  @Test
  @TestTransaction
  void shouldCreateProfileSuccessfully() {
    ProfileCreateRequest request = generateProfileCreateRequest();

    ProfileResponse response =
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(basicPath)
            .then()
            .statusCode(201)
            .extract()
            .as(ProfileResponse.class);

    createdProfileIds.add(response.id());

    assertThat(response.id(), notNullValue());
    assertThat(response.name(), is(request.name()));
    assertThat(response.location(), is(request.location()));
    assertThat(response.skill(), is(request.skill()));
    assertThat(response.yearsOfExperience(), is(request.yearsOfExperience()));
    assertThat(response.description(), is(request.description()));
    assertThat(response.dateOfBirth(), is(request.dateOfBirth()));
    assertThat(response.instruments(), is(request.instruments()));
    assertThat(response.genres(), is(request.genres()));
  }

  @Test
  @TestTransaction
  void shouldFindProfileByIdSuccessfully() {
    ProfileEntity existing = persistRandomProfile("John Doe");

    ProfileResponse response =
        given()
            .when()
            .get(basicPath + "/{id}", existing.getId())
            .then()
            .statusCode(200)
            .extract()
            .as(ProfileResponse.class);

    assertThat(response.id(), is(existing.getId()));
    assertThat(response.name(), is(existing.getName()));
    assertThat(response.location(), is(existing.getLocation()));
    assertThat(response.skill(), is(existing.getSkill()));
    assertThat(response.yearsOfExperience(), is(existing.getYearsOfExperience()));
    assertThat(response.description(), is(existing.getDescription()));
    assertThat(response.dateOfBirth().toInstant(), is(existing.getDateOfBirth()));
    assertThat(
        response.dateOfBirth(), is(OffsetDateTime.ofInstant(existing.getDateOfBirth(), UTC)));
    assertThat(response.instruments(), is(existing.getInstruments()));
    assertThat(response.genres(), is(existing.getGenres()));
  }

  @Test
  @TestTransaction
  void shouldUpdateProfileSuccessfully() {
    ProfileEntity existing = persistRandomProfile("Old Name");
    ProfileUpdateRequest request =
        new ProfileUpdateRequest(
            "New Name",
            "New location",
            SkillLevel.ADVANCED,
            2,
            "New desc",
            OffsetDateTime.now(),
            Set.of("DRUMS"),
            Set.of("ROCK"));

    ProfileResponse response =
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .put(basicPath + "/{id}", existing.getId())
            .then()
            .statusCode(200)
            .extract()
            .as(ProfileResponse.class);

    assertThat(response.id(), is(existing.getId()));
    assertThat(response.name(), is(request.name()));
    assertThat(response.location(), is(request.location()));
    assertThat(response.skill(), is(request.skill()));
    assertThat(response.yearsOfExperience(), is(request.yearsOfExperience()));
    assertThat(response.description(), is(request.description()));
    assertThat(response.dateOfBirth().toInstant(), is(request.dateOfBirth().toInstant()));
    assertThat(response.instruments(), is(request.instruments()));
    assertThat(response.genres(), is(request.genres()));
  }

  @Test
  @TestTransaction
  void shouldFindAllWithPagination() {
    ProfileEntity entityA = persistRandomProfile("Artist A");
    ProfileEntity entityB = persistRandomProfile("Artist B");
    ProfileEntity entityC = persistRandomProfile("Artist C");

    var a =
        profileRepository.findAll(
            SearchFilter.builder().build(), 0, 10000, "name", Sort.Direction.Ascending);
    a.data().forEach(it -> System.out.println(it.getName()));

    PagedResponse<ProfileResponse> response =
        given()
            .queryParam("page", 0)
            .queryParam("size", 2)
            .queryParam("sort", "name")
            .queryParam("direction", "Ascending")
            .when()
            .get(basicPath)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<PagedResponse<ProfileResponse>>() {});

    assertThat(
        response.data().stream().map(ProfileResponse::id).toList(),
        contains(entityA.getId(), entityB.getId()));
    assertThat(response.totalCount(), is(greaterThanOrEqualTo(3L)));
    assertThat(response.page(), is(0));
    assertThat(response.size(), is(2));
    assertThat(response.pagesCount(), is(2));
  }

  @Test
  @TestTransaction
  void shouldFilterBySkillAndLocation() {
    QuarkusTransaction.requiringNew()
        .run(
            () -> {
              ProfileEntity artist = persistRandomProfile("Unique Artist");
              artist.setSkill(SkillLevel.ADVANCED);
              artist.setLocation("Berlin");
              profileRepository.update(artist);
            });

    PagedResponse<ProfileResponse> response =
        given()
            .queryParam("skill", "ADVANCED")
            .queryParam("location", "Berlin")
            .when()
            .get(basicPath)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<PagedResponse<ProfileResponse>>() {});

    assertThat(response.data().get(0).name(), is("Unique Artist"));
    assertThat(response.totalCount(), is(1L));
  }

  @Test
  @TestTransaction
  void shouldFilterByInstrumentsAndGenres() {
    InstrumentEntity guitar = persistInstrument("name");
    GenreEntity metal = persistGenre("name");

    QuarkusTransaction.requiringNew()
        .run(
            () -> {
              ProfileEntity musician = persistRandomProfile("Blues Man");
              musician.setInstruments(new HashSet<>(Set.of(guitar)));
              musician.setGenres(new HashSet<>(Set.of(metal)));
              profileRepository.update(musician);
            });

    PagedResponse<ProfileResponse> response =
        given()
            .queryParam("instruments", "name")
            .queryParam("genres", "name")
            .when()
            .get(basicPath)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<PagedResponse<ProfileResponse>>() {});

    assertThat(response.totalCount(), is(1L));
    assertThat(response.data().get(0).name(), is("Blues Man"));
  }

  @Test
  @TestTransaction
  void shouldReturnEmptyWhenNoMatchFound() {
    persistRandomProfile("Someone");

    PagedResponse<ProfileResponse> response =
        given()
            .queryParam("name", "NonExistentName")
            .when()
            .get(basicPath)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<PagedResponse<ProfileResponse>>() {});

    assertThat(response.data().isEmpty(), is(true));
    assertThat(response.totalCount(), is(0L));
  }

  @Test
  @TestTransaction
  void shouldHandleNullFiltersAndReturnAll() {
    persistRandomProfile("Profile 1");
    persistRandomProfile("Profile 2");

    PagedResponse<ProfileResponse> response =
        given()
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get(basicPath)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<PagedResponse<ProfileResponse>>() {});

    assertThat(response.totalCount(), is(greaterThanOrEqualTo(2L)));
  }
}
