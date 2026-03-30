package jammy.platform.resources;

import static io.restassured.RestAssured.given;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import jammy.platform.entities.ProfileEntity;
import jammy.platform.enums.SkillLevel;
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
import org.junit.jupiter.api.Test;

@QuarkusTest
@AllArgsConstructor
public class ProfileControllerTest {

  private final ProfileRepository profileRepository;

  private final String basicPath = "/profiles";

  @Transactional
  ProfileEntity persistRandomProfile(String name) {
    Instant dateOfBirth = Instant.parse("2000-01-01T00:00:00Z");
    ProfileEntity entity =
        ProfileEntity.builder()
            .id(UUID.randomUUID())
            .name(name)
            .location("Lviv")
            .skill(SkillLevel.INTERMEDIATE)
            .dateOfBirth(dateOfBirth)
            .instruments(new HashSet<>())
            .genres(new HashSet<>())
            .build();
    profileRepository.create(entity);
    return entity;
  }

  @Transactional
  ProfileCreateRequest generateProfileCreateRequest() {
    String name = "Matt Tuck";
    String location = "London";
    SkillLevel skillLevel = SkillLevel.INTERMEDIATE;
    int yearsOfExperience = 2;
    String description = "Description";
    //        Instant dateOfBirth = Instant.parse("2000-01-01T00:00:00Z");
    OffsetDateTime dateOfBirth = OffsetDateTime.parse("2000-01-01T00:00:00Z");
    Set<String> instruments = new HashSet<>();
    instruments.add("GUITAR");
    Set<String> genres = new HashSet<>();
    genres.add("METAL");
    return new ProfileCreateRequest(
        name,
        location,
        skillLevel,
        yearsOfExperience,
        description,
        dateOfBirth,
        instruments,
        genres);
  }

  @Test
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
  void shouldFindAllWithPagination() {
    ProfileEntity entityA = persistRandomProfile("Artist A");
    ProfileEntity entityB = persistRandomProfile("Artist B");
    ProfileEntity entityC = persistRandomProfile("Artist C");

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
}
