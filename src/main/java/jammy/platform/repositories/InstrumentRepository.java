package jammy.platform.repositories;

import jammy.platform.entities.InstrumentEntity;
import java.util.List;
import java.util.Set;

public interface InstrumentRepository {
  List<InstrumentEntity> findByNameIn(Set<String> names);
}
