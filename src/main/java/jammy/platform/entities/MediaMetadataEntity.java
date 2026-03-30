package jammy.platform.entities;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "media_metadata")
@Getter
@Setter
public class MediaMetadataEntity {
  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id")
  private ProfileEntity profile;

  private String type;
  private String url;

  @Column(name = "is_primary")
  private Boolean isPrimary;
}
