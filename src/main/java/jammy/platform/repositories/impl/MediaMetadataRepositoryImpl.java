package jammy.platform.repositories.impl;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jammy.platform.entities.MediaMetadataEntity;
import jammy.platform.repositories.MediaMetadataRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor
public class MediaMetadataRepositoryImpl
    implements MediaMetadataRepository, PanacheRepositoryBase<MediaMetadataEntity, UUID> {
  public void create(MediaMetadataEntity entity) {
    persist(entity);
  }
}
