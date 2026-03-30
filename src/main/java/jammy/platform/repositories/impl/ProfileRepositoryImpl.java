package jammy.platform.repositories.impl;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jammy.platform.entities.ProfileEntity;
import jammy.platform.repositories.ProfileRepository;
import jammy.platform.responses.PagedResponse;
import java.util.UUID;

@ApplicationScoped
public class ProfileRepositoryImpl
    implements ProfileRepository, PanacheRepositoryBase<ProfileEntity, UUID> {

  @Override
  @Transactional
  public ProfileEntity create(ProfileEntity profile) {
    persist(profile);
    return profile;
  }

  @Override
  @Transactional
  public ProfileEntity update(ProfileEntity profile) {
    return getEntityManager().merge(profile);
  }

  @Override
  public PagedResponse<ProfileEntity> findAll(
      int page, int size, String sort, Sort.Direction direction) {
    var query = findAll(Sort.by(sort, direction));
    var pagedQuery = query.page(page, size);
    var pageCount = query.pageCount();

    return new PagedResponse<>(pagedQuery.list(), query.count(), page, size, pageCount);
  }

  @Override
  public ProfileEntity findById(UUID profileId) {
    return findByIdOptional(profileId).orElse(null);
  }
}
