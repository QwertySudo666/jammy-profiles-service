package jammy.platform.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jammy.platform.requests.ProfileCreateRequest;
import jammy.platform.requests.ProfileUpdateRequest;
import jammy.platform.responses.PagedResponse;
import jammy.platform.responses.ProfileResponse;
import jammy.platform.services.ProfileService;
import java.util.UUID;
import lombok.AllArgsConstructor;

@Path("/profiles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  @POST
  public Response createProfile(ProfileCreateRequest request) {
    ProfileResponse response = profileService.create(request);

    return Response.status(Response.Status.CREATED).entity(response).build();
  }

  @GET
  @Path("/{id}")
  public ProfileResponse getProfile(@PathParam("id") UUID id) {
    return profileService.findById(id);
  }

  @GET
  public PagedResponse<ProfileResponse> getAll(
      @QueryParam("page") @DefaultValue("0") int page,
      @QueryParam("size") @DefaultValue("20") int size) {
    return profileService.findAll(page, size);
  }

  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, ProfileUpdateRequest request) {
    ProfileResponse response = profileService.update(id, request);
    return Response.ok().entity(response).build();
  }
}
