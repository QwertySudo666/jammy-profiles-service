package jammy.platform.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // 1. Log the REAL error for you to see in the console
        log.error("Unhandled application error: ", exception);

        // 2. Return a "Polite" error to the user
        ErrorResponse error = new ErrorResponse("An unexpected error occurred. Please try again later.");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR) // 500
                .entity(error)
                .build();
    }
}