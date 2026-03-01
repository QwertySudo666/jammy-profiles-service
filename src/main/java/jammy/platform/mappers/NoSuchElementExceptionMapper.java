package jammy.platform.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.NoSuchElementException;

@Provider
public class NoSuchElementExceptionMapper implements ExceptionMapper<NoSuchElementException> {

    @Override
    public Response toResponse(NoSuchElementException exception) {
        // Log the error if needed
        return Response.status(Response.Status.NOT_FOUND) // 404
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}