package jammy.platform.repositories;

import jammy.platform.entities.InstrumentEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface InstrumentRepository {
  List<InstrumentEntity> findByNameIn(Set<String> names);

  InstrumentEntity create(String name);

  void delete(UUID id);
}
