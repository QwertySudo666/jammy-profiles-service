package jammy.platform.repositories.impl;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jammy.platform.entities.ProfileEntity;
import jammy.platform.models.SearchFilter;
import jammy.platform.repositories.ProfileRepository;
import jammy.platform.responses.PagedResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
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
      SearchFilter filter, int page, int size, String sort, Sort.Direction direction) {

    // 1. Початковий запит
    StringBuilder hql = new StringBuilder("from ProfileEntity p where 1=1");
    Map<String, Object> params = new HashMap<>();

    // 2. Додаємо фільтри лише якщо вони не null
    if (filter.getName() != null && !filter.getName().isBlank()) {
      hql.append(" AND lower(p.name) LIKE lower(CONCAT('%', :name, '%'))");
      params.put("name", filter.getName());
    }

    if (filter.getLocation() != null) {
      hql.append(" AND lower(p.location) LIKE lower(CONCAT('%', :location, '%'))");
      params.put("location", filter.getLocation());
    }

    if (filter.getSkill() != null) {
      hql.append(" AND p.skill = :skill");
      params.put("skill", filter.getSkill());
    }

    if (filter.getMinExperience() != null) {
      hql.append(" AND p.yearsOfExperience >= :minExp");
      params.put("minExp", filter.getMinExperience());
    }

    if (filter.getMinAge() != null) {
      Instant ageLimit =
          Instant.now().atZone(ZoneOffset.UTC).minusYears(filter.getMinAge()).toInstant();
      hql.append(" AND p.dateOfBirth <= :ageLimit");
      params.put("ageLimit", ageLimit);
    }

    // Фільтр по інструментах (тільки якщо сет не порожній)
    if (filter.getInstruments() != null && !filter.getInstruments().isEmpty()) {
      hql.append(" AND EXISTS (SELECT i FROM p.instruments i WHERE i.name IN :instruments)");
      params.put("instruments", filter.getInstruments());
    }

    // Фільтр по жанрах
    if (filter.getGenres() != null && !filter.getGenres().isEmpty()) {
      hql.append(" AND EXISTS (SELECT g FROM p.genres g WHERE g.name IN :genres)");
      params.put("genres", filter.getGenres());
    }

    // 3. Виконуємо запит через Panache
    var panacheQuery = find(hql.toString(), Sort.by(sort, direction), params).page(page, size);

    return new PagedResponse<>(
        panacheQuery.list(), panacheQuery.count(), page, size, panacheQuery.pageCount());
  }

  @Override
  public ProfileEntity findById(UUID profileId) {
    return findByIdOptional(profileId).orElse(null);
  }

  @Override
  public void delete(UUID profileId) {
    deleteById(profileId);
  }
}
