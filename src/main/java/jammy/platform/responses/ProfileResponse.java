package jammy.platform.responses;

import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String name
) {
}
