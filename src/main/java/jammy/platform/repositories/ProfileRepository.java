package jammy.platform.repositories;

import jammy.platform.models.Profile;
import jammy.platform.responses.PagedResponse;
import java.util.UUID;

public interface ProfileRepository {
  public Profile create(Profile profile);

  public void update(Profile profile);

  public PagedResponse<Profile> findAll(int page, int size);

  public Profile findById(UUID profileId);
}
