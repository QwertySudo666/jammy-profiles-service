package jammy.platform.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jammy.platform.requests.ProfileUpdateRequest;
import jammy.platform.responses.PagedResponse;
import jammy.platform.services.ProfileService;
import jammy.platform.requests.ProfileCreateRequest;
import jammy.platform.responses.ProfileResponse;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Path("/profiles") // The base URL for all methods in this class
@Produces(MediaType.APPLICATION_JSON) // We always send JSON
@Consumes(MediaType.APPLICATION_JSON) // We always receive JSON
@AllArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @POST
    public Response createProfile(ProfileCreateRequest request) {
        // 1. Call the service to do the work
        ProfileResponse response = profileService.create(request);

        // 2. Return HTTP 201 (Created) with the body
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}")
    public ProfileResponse getProfile(@PathParam("id") UUID id) {
        // Quarkus is smart: if the service throws an exception,
        // it will catch it. If it returns a result, it sends 200 OK.
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
        profileService.update(id, request);
        return Response.noContent().build(); // 204 No Content is standard for updates
    }
}
