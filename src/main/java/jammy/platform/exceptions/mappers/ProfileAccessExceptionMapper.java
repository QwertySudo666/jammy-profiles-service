package jammy.platform.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jammy.platform.exceptions.ProfileAccessDeniedException;

import java.util.Map;

@Provider
public class ProfileAccessExceptionMapper implements ExceptionMapper<ProfileAccessDeniedException> {

    @Override
    public Response toResponse(ProfileAccessDeniedException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(Map.of(
                        "code", "Access Denied",
                        "message", exception.getMessage()
                ))
                .build();
    }
}