package jammy.platform.repositories;

import io.quarkus.panache.common.Sort;
import jammy.platform.entities.ProfileEntity;
import jammy.platform.responses.PagedResponse;
import java.util.UUID;

public interface ProfileRepository {
  ProfileEntity create(ProfileEntity profile);

  ProfileEntity update(ProfileEntity profile);

  PagedResponse<ProfileEntity> findAll(int page, int size, String sort, Sort.Direction direction);

  ProfileEntity findById(UUID profileId);
}
