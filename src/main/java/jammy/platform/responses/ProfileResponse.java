package jammy.platform.responses;

import jammy.platform.enums.SkillLevel;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record ProfileResponse(
    UUID id,
    String name,
    String location,
    SkillLevel skill,
    Integer yearsOfExperience,
    String description,
    OffsetDateTime dateOfBirth,
    Set<String> instruments,
    Set<String> genres,
    String avatarUrl
    //        List<MediaMetadata> media,
    //        List<ProfileLink> links
    ) {}
