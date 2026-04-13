package jammy.platform.resources;

import jakarta.ws.rs.*;
import jammy.platform.services.S3Service;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@Path("/media")
// @Produces(MediaType.APPLICATION_JSON)
// @Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class MediaController {

  private final S3Service s3Service;

  @GET
  @Path("/presigned-url")
  public Map<String, String> getPresignedPutUrl(@QueryParam("fileName") String fileName) {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("uploaded-by", "quarkus-app");

    String url = s3Service.createPresignedUrl(fileName, metadata);
    return Map.of("url", url);
  }
}
