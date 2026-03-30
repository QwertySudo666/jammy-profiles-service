package jammy.platform.entities;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profile_links")
@Getter
@Setter
public class ProfileLinkEntity {
  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id")
  private ProfileEntity profile;

  @Column(name = "link_type")
  private String linkType;

  private String url;
  private String title;
}
