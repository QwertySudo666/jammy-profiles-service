package jammy.platform.repositories;

import io.quarkus.panache.common.Sort;
import jammy.platform.entities.ProfileEntity;
import jammy.platform.models.SearchFilter;
import jammy.platform.responses.PagedResponse;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {
  ProfileEntity create(ProfileEntity profile);

  ProfileEntity update(ProfileEntity profile);

  PagedResponse<ProfileEntity> findAll(
      SearchFilter filter, int page, int size, String sort, Sort.Direction direction);

  ProfileEntity findById(UUID profileId);

  void delete(UUID profileId);

  boolean existsByUserId(UUID userId);

  Optional<ProfileEntity> findByUserId(UUID userId);
}
