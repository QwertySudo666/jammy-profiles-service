package jammy.platform.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jammy.platform.exceptions.ProfileAlreadyExistsException;

@Provider
public class ProfileAlreadyExistsMapper implements ExceptionMapper<ProfileAlreadyExistsException> {
    @Override
    public Response toResponse(ProfileAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(exception.getMessage())
                .build();
    }
}
