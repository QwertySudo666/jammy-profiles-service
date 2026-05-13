package jammy.platform.services;

import io.quarkus.panache.common.Sort;
import jakarta.inject.Singleton;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jammy.platform.entities.GenreEntity;
import jammy.platform.entities.InstrumentEntity;
import jammy.platform.entities.MediaMetadataEntity;
import jammy.platform.entities.ProfileEntity;
import jammy.platform.enums.SkillLevel;
import jammy.platform.exceptions.ProfileAccessDeniedException;
import jammy.platform.exceptions.ProfileAlreadyExistsException;
import jammy.platform.models.SearchFilter;
import jammy.platform.repositories.GenreRepository;
import jammy.platform.repositories.InstrumentRepository;
import jammy.platform.repositories.MediaMetadataRepository;
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
  private final MediaMetadataRepository mediaRepository;

  /**
   * Orchestrates the creation of a new profile. Generates the ID here so the service (business
   * logic) owns the identity, not the database.
   */
  @Transactional
  public ProfileResponse create(UUID userId, ProfileCreateRequest request) {
    if (profileRepository.existsByUserId(userId)) {
      throw new ProfileAlreadyExistsException();
    }

    List<InstrumentEntity> instrumentEntities =
        instrumentRepository.findByNameIn(request.instruments());
    List<GenreEntity> genreEntities = genreRepository.findByNameIn(request.genres());

    ProfileEntity profile = mapToDomain(userId, request);
    profile.setInstruments(new HashSet<>(instrumentEntities));
    profile.setGenres(new HashSet<>(genreEntities));
    if (request.imageUrl() != null) {
      profile.setAvatar(request.imageUrl());
    }
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

  public PagedResponse<ProfileResponse> findAll(
      SearchFilter filter, int page, int size, String sort, Sort.Direction direction) {
    int pageSize = (size <= 0 || size > 100) ? 20 : size;
    int pageNum = Math.max(0, page);

    PagedResponse<ProfileEntity> response =
        profileRepository.findAll(filter, pageNum, pageSize, sort, direction);
    return new PagedResponse<ProfileResponse>(
        response.data().stream().map(ProfileService::mapToResponse).toList(),
        response.totalCount(),
        response.page(),
        response.size(),
        response.pagesCount());
  }

  @Transactional
  public ProfileResponse update(UUID userId, UUID profileId, ProfileUpdateRequest request) {
    ProfileEntity existing = profileRepository.findById(profileId);
    if (existing == null) throw new NotFoundException("Not found");
    if (!existing.getUserId().equals(userId))
      throw new ProfileAccessDeniedException("You can edit only your profile");

    Set<InstrumentEntity> instrumentEntities =
        new HashSet<>(instrumentRepository.findByNameIn(request.instruments()));
    Set<GenreEntity> genreEntities = new HashSet<>(genreRepository.findByNameIn(request.genres()));

    OffsetDateTime dateOfBirth = request.dateOfBirth();

    existing.setName(request.name());
    existing.setLocation(request.location());
    existing.setSkill(request.skill());
    existing.setYearsOfExperience(request.yearsOfExperience());
    existing.setDescription(request.description());
    existing.setInstruments(instrumentEntities);
    existing.setGenres(genreEntities);

    if (dateOfBirth != null) {
      existing.setDateOfBirth(dateOfBirth.toInstant());
    }

    if (request.imageUrl() != null) {
      updateAvatar(existing, request.imageUrl());
    }

    ProfileEntity updated = profileRepository.update(existing);
    return mapToResponse(updated);
  }

  public UUID findByUserId(UUID userId) {
    return profileRepository.findByUserId(userId).map(ProfileEntity::getId).orElse(null);
  }

  private void updateAvatar(ProfileEntity profile, String newUrl) {
    profile.getMedia().stream()
        .filter(MediaMetadataEntity::getIsPrimary)
        .findFirst()
        .ifPresentOrElse(avatar -> avatar.setUrl(newUrl), () -> profile.setAvatar(newUrl));
  }

  private static ProfileEntity mapToDomain(UUID userId, ProfileCreateRequest request) {
    String name = request.name();
    String location = request.location();
    SkillLevel skill = request.skill();
    int yearsOfExperience = request.yearsOfExperience();
    String description = request.description();
    Instant dateOfBirth =
        (request.dateOfBirth() != null) ? request.dateOfBirth().toInstant() : null;
    return ProfileEntity.builder()
        .id(UUID.randomUUID())
        .userId(userId)
        .name(name)
        .location(location)
        .skill(skill)
        .yearsOfExperience(yearsOfExperience)
        .description(description)
        .dateOfBirth(dateOfBirth)
        .build();
  }

  private static ProfileResponse mapToResponse(ProfileEntity profile) {
    Set<String> instruments =
        profile.getInstruments().stream()
            .map(InstrumentEntity::getName)
            .collect(Collectors.toSet());
    Set<String> genres =
        profile.getGenres().stream().map(GenreEntity::getName).collect(Collectors.toSet());

    OffsetDateTime dateOfBirth =
        (profile.getDateOfBirth() != null)
            ? OffsetDateTime.ofInstant(profile.getDateOfBirth(), ZoneOffset.UTC)
            : null;

    String avatarUrl =
        profile.getMedia().stream()
            .filter(MediaMetadataEntity::getIsPrimary)
            .findFirst()
            .map(MediaMetadataEntity::getUrl)
            .orElse(null);

    return new ProfileResponse(
        profile.getId(),
        profile.getName(),
        profile.getLocation(),
        profile.getSkill(),
        profile.getYearsOfExperience(),
        profile.getDescription(),
        dateOfBirth,
        instruments,
        genres,
        avatarUrl
        //                profile.getMedia(),
        //                profile.getLinks()
        );
  }
}
