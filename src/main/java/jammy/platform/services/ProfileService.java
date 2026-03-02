package jammy.platform.services;

import jakarta.inject.Singleton;
import jammy.platform.models.Profile;
import jammy.platform.repositories.ProfileRepository;
import jammy.platform.requests.ProfileCreateRequest;
import jammy.platform.requests.ProfileUpdateRequest;
import jammy.platform.responses.PagedResponse;
import jammy.platform.responses.ProfileResponse;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.AllArgsConstructor;

@Singleton
@AllArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;

  /**
   * Orchestrates the creation of a new profile. Generates the ID here so the service (business
   * logic) owns the identity, not the database.
   */
  public ProfileResponse create(ProfileCreateRequest request) {
    Profile profile = mapToDomain(request);
    Profile created = profileRepository.create(profile);
    return mapToResponse(created);
  }

  /**
   * Handles the lookup logic. Note: We use a custom exception here so the REST layer can map it to
   * a 404 automatically.
   */
  public ProfileResponse findById(UUID id) {
    Profile profile = profileRepository.findById(id);

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

    PagedResponse<Profile> response = profileRepository.findAll(pageNum, pageSize);
    return new PagedResponse<ProfileResponse>(
        response.data().stream().map(ProfileService::mapToResponse).toList(),
        response.totalCount(),
        response.page(),
        response.size());
  }

  public void update(UUID id, ProfileUpdateRequest request) {
    Profile existing = profileRepository.findById(id);
    if (existing == null) throw new NoSuchElementException("Not found");

    // Create updated domain object
    Profile updated = Profile.builder().id(id).name(request.name()).build();

    profileRepository.update(updated);
  }

  private static Profile mapToDomain(ProfileCreateRequest request) {
    return Profile.builder()
        .id(UUID.randomUUID()) // Identity generation happens in the Service
        .name(request.name())
        .build();
  }

  private static ProfileResponse mapToResponse(Profile profile) {
    return new ProfileResponse(profile.getId(), profile.getName());
  }
}
