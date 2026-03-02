package jammy.platform.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jammy.platform.models.Profile;
import jammy.platform.repositories.ProfileRepository;
import jammy.platform.requests.ProfileCreateRequest;
import jammy.platform.requests.ProfileUpdateRequest;
import jammy.platform.responses.PagedResponse;
import jammy.platform.responses.ProfileResponse;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

@QuarkusTest
@AllArgsConstructor
public class ProfileControllerTest {
  private final ProfileRepository profileRepository;
  private final String basicPath = "/profiles";

  @Test
  void shouldCreateProfileSuccessfully() {
    ProfileCreateRequest request = new ProfileCreateRequest("Santa");

    given()
        .contentType(ContentType.JSON) // Tell the API we are sending JSON
        .body(request)
        .when()
        .post(basicPath)
        .then()
        .statusCode(201) // Matches your Response.Status.CREATED
        .body("id", notNullValue()) // Use the standard Hamcrest matcher
        .body("name", is("Santa"));
  }

  @Test
  void shouldFindProfileByIdSuccessfully() {
    Profile profile = new Profile(UUID.randomUUID(), "Santa");
    Profile existing = profileRepository.create(profile);

    given()
        .contentType(ContentType.JSON) // Tell the API we are sending JSON
        .when()
        .get(basicPath + "/" + existing.getId())
        .then()
        .statusCode(200) // Matches your Response.Status.CREATED
        .body("id", is(existing.getId().toString())) // Use the standard Hamcrest matcher
        .body("name", is(existing.getName()));
  }

  @Test
  void shouldReturn404WhenProfileNotFound() {
    String randomId = "550e8400-e29b-41d4-a716-446655440000";

    given()
        .pathParam("id", randomId)
        .when()
        .get(basicPath + "/{id}")
        .then()
        .statusCode(404); // This will work if you add the ExceptionMapper
  }

  @Test
  void shouldUpdateProfileSuccessfully() {
    // 1. Arrange: Create a profile directly in the DB
    Profile profile = new Profile(UUID.randomUUID(), "Old Name");
    profileRepository.create(profile);

    ProfileUpdateRequest updateRequest = new ProfileUpdateRequest("New Name");

    // 2. Act & Assert
    given()
        .contentType(ContentType.JSON)
        .body(updateRequest)
        .when()
        .put(basicPath + "/" + profile.getId())
        .then()
        .statusCode(204); // No Content

    // 3. Verify in DB
    Profile updated = profileRepository.findById(profile.getId());
    assertThat(updated.getName(), is("New Name"));
  }

  @Test
  void shouldReturn404WhenUpdatingNonExistentProfile() {
    UUID randomId = UUID.randomUUID();
    ProfileUpdateRequest updateRequest = new ProfileUpdateRequest("Ghost");

    given()
        .contentType(ContentType.JSON)
        .body(updateRequest)
        .when()
        .put(basicPath + "/" + randomId)
        .then()
        .statusCode(404);
  }

  @Test
  void shouldFindAllProfilesWithPaginationByStringComparison() throws JsonProcessingException {
    // 1. Arrange: Create data
    UUID idA = UUID.randomUUID();
    UUID idB = UUID.randomUUID();
    profileRepository.create(new Profile(idA, "Artist A"));
    profileRepository.create(new Profile(idB, "Artist B"));
    profileRepository.create(new Profile(UUID.randomUUID(), "Artist C"));

    // 2. Build the expected object (This matches your PagedResponse structure)
    List<ProfileResponse> expectedData =
        List.of(new ProfileResponse(idA, "Artist A"), new ProfileResponse(idB, "Artist B"));
    // Note: totalCount is 3 because we have 3 artists in the DB total
    PagedResponse<ProfileResponse> expectedObject = new PagedResponse<>(expectedData, 3, 0, 2);

    // 3. Convert expected object to JSON String
    ObjectMapper mapper = new ObjectMapper();
    String expectedJson = mapper.writeValueAsString(expectedObject);

    // 4. Act & Assert
    given()
        .queryParam("page", 0)
        .queryParam("size", 2)
        .when()
        .get(basicPath)
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .extract()
        .asString() // Get the raw response body as a String
        .equals(expectedJson); // Direct string comparison
  }

  @Test
  void shouldReturnEmptyListWhenPageIsOutOfBounds() {
    given()
        .queryParam("page", 999)
        .queryParam("size", 10)
        .when()
        .get(basicPath)
        .then()
        .statusCode(200)
        .body("totalCount", is(0));
  }
}
