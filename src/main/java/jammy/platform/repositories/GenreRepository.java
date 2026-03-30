package jammy.platform.repositories;

import jammy.platform.entities.GenreEntity;
import java.util.List;
import java.util.Set;

public interface GenreRepository {
  List<GenreEntity> findByNameIn(Set<String> names);
}
