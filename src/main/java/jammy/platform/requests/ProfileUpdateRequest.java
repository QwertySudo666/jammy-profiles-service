package jammy.platform.requests;

import jammy.platform.enums.SkillLevel;
import java.time.OffsetDateTime;
import java.util.Set;

public record ProfileUpdateRequest(
    String name,
    String location,
    SkillLevel skill,
    Integer yearsOfExperience,
    String description,
    OffsetDateTime dateOfBirth,
    Set<String> instruments,
    Set<String> genres,
    String imageUrl) {}
