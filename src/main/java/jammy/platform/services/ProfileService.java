package jammy.platform.services;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jammy.platform.entities.GenreEntity;
import jammy.platform.entities.InstrumentEntity;
import jammy.platform.entities.ProfileEntity;
import jammy.platform.enums.SkillLevel;
import jammy.platform.repositories.GenreRepository;
import jammy.platform.repositories.InstrumentRepository;
import jammy.platform.repositories.ProfileRepository;
import jammy.platform.requests.ProfileCreateRequest;
import jammy.platform.requests.ProfileUpdateRequest;
import jammy.platform.responses.PagedResponse;
import jammy.platform.responses.ProfileResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@Singleton
@AllArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final InstrumentRepository instrumentRepository;
  private final GenreRepository genreRepository;

  /**
   * Orchestrates the creation of a new profile. Generates the ID here so the service (business
   * logic) owns the identity, not the database.
   */
  @Transactional
  public ProfileResponse create(ProfileCreateRequest request) {
    List<InstrumentEntity> instrumentEntities =
        instrumentRepository.findByNameIn(request.instruments());
    List<GenreEntity> genreEntities = genreRepository.findByNameIn(request.genres());
    ProfileEntity profile =
        mapToDomain(request, new HashSet<>(instrumentEntities), new HashSet<>(genreEntities));
    ProfileEntity created = profileRepository.create(profile);
    return mapToResponse(created);
  }

  /**
   * Handles the lookup logic. Note: We use a custom exception here so the REST layer can map it to
   * a 404 automatically.
   */
  public ProfileResponse findById(UUID id) {
    ProfileEntity profile = profileRepository.findById(id);

    if (profile == null) {
      throw new NoSuchElementException("Profile with ID " + id + " not found");
    }

    return mapToResponse(profile);
  }

  // --- Mappers (Clean & Isolated) ---

  // Add to ProfileService.java
  public PagedResponse<ProfileResponse> findAll(int page, int size) {
    // Basic validation
    int pageSize = (size <= 0 || size > 100) ? 20 : size;
    int pageNum = Math.max(0, page);

    PagedResponse<ProfileEntity> response = profileRepository.findAll(pageNum, pageSize);
    return new PagedResponse<ProfileResponse>(
        response.data().stream().map(ProfileService::mapToResponse).toList(),
        response.totalCount(),
        response.page(),
        response.size(),
        response.pagesCount());
  }

  @Transactional
  public ProfileResponse update(UUID id, ProfileUpdateRequest request) {
    ProfileEntity existing = profileRepository.findById(id);
    if (existing == null) throw new NoSuchElementException("Not found");

    Set<InstrumentEntity> instrumentEntities =
        new HashSet<>(instrumentRepository.findByNameIn(request.instruments()));
    Set<GenreEntity> genreEntities = new HashSet<>(genreRepository.findByNameIn(request.genres()));

    existing.setName(request.name());
    existing.setLocation(request.location());
    existing.setSkill(request.skill());
    existing.setYearsOfExperience(request.yearsOfExperience());
    existing.setDescription(request.description());
    existing.setDateOfBirth(request.dateOfBirth().toInstant());
    existing.setInstruments(instrumentEntities);
    existing.setGenres(genreEntities);

    ProfileEntity updated = profileRepository.update(existing);
    return mapToResponse(updated);
  }

  private static ProfileEntity mapToDomain(
      ProfileCreateRequest request,
      Set<InstrumentEntity> instrumentEntities,
      Set<GenreEntity> genreEntities) {
    String name = request.name();
    String location = request.location();
    SkillLevel skill = request.skill();
    int yearsOfExperience = request.yearsOfExperience();
    String description = request.description();
    Instant dateOfBirth = request.dateOfBirth().toInstant();
    return ProfileEntity.builder()
        .id(UUID.randomUUID())
        .name(name)
        .location(location)
        .skill(skill)
        .yearsOfExperience(yearsOfExperience)
        .description(description)
        .dateOfBirth(dateOfBirth)
        .instruments(instrumentEntities)
        .genres(genreEntities)
        .build();
  }

  private static ProfileResponse mapToResponse(ProfileEntity profile) {
    Set<String> instruments =
        profile.getInstruments().stream()
            .map(InstrumentEntity::getName)
            .collect(Collectors.toSet());
    Set<String> genres =
        profile.getGenres().stream().map(GenreEntity::getName).collect(Collectors.toSet());

    return new ProfileResponse(
        profile.getId(),
        profile.getName(),
        profile.getLocation(),
        profile.getSkill(),
        profile.getYearsOfExperience(),
        profile.getDescription(),
        OffsetDateTime.ofInstant(profile.getDateOfBirth(), ZoneOffset.UTC),
        instruments,
        genres
        //                profile.getMedia(),
        //                profile.getLinks()
        );
  }
}
