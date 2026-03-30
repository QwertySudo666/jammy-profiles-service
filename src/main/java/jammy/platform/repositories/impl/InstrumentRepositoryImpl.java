package jammy.platform.repositories.impl;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jammy.platform.entities.InstrumentEntity;
import jammy.platform.repositories.InstrumentRepository;
import java.util.*;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor
public class InstrumentRepositoryImpl
    implements InstrumentRepository, PanacheRepositoryBase<InstrumentEntity, UUID> {

  public List<InstrumentEntity> findByNameIn(Set<String> names) {
    if (names == null || names.isEmpty()) {
      return Collections.emptyList();
    }

    return list("name in :names", Map.of("names", names));
  }
}
