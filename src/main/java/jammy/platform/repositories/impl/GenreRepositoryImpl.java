package jammy.platform.repositories.impl;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jammy.platform.entities.GenreEntity;
import jammy.platform.repositories.GenreRepository;
import java.util.*;

@ApplicationScoped
public class GenreRepositoryImpl
    implements GenreRepository, PanacheRepositoryBase<GenreEntity, UUID> {
  public List<GenreEntity> findByNameIn(Set<String> names) {
    if (names == null || names.isEmpty()) {
      return Collections.emptyList();
    }

    return list("name in :names", Map.of("names", names));
  }

  @Override
  public GenreEntity create(String name) {
    GenreEntity e = new GenreEntity(UUID.randomUUID(), name);
    persist(e);
    return e;
  }

  @Override
  public void delete(UUID id) {
    deleteById(id);
  }
}
