package jammy.platform.responses;

import java.util.UUID;

public record MyProfileStatusResponse(
    UUID userId, UUID profileId, String email, String name, String avatarUrl) {}
