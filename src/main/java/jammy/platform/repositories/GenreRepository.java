package jammy.platform.repositories;

import jammy.platform.entities.GenreEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GenreRepository {
  List<GenreEntity> findByNameIn(Set<String> names);

  GenreEntity create(String name);

  void delete(UUID id);
}
